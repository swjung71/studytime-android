package kr.co.digitalanchor.studytime.purchase;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.GoodsResult;
import kr.co.digitalanchor.studytime.model.Item;
import kr.co.digitalanchor.studytime.model.ParentModel;
import kr.co.digitalanchor.studytime.model.SetCoin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.AndroidUtils;
import kr.co.digitalanchor.utils.IabHelper;
import kr.co.digitalanchor.utils.IabResult;
import kr.co.digitalanchor.utils.Inventory;
import kr.co.digitalanchor.utils.Purchase;

/**
 * Created by Thomas on 2015-08-25.
 */
public class PurchaseActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private final int REQUEST_UPDATE_COIN = 50001;

    // Debug tag, for logging
    static final String TAG = "PurchaseActivity";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    ListView listView;

    List<Item> items;

    ItemAdapter itemAdapter;

    Button offerwall;

    String selectedKey;
    int selectedHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_purchase);

        String base64EncodedPublicKey = getString(R.string.key);

        // Create the helper, passing it our context and the public key to verify signatures with
        Logger.d("Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Logger.d("Starting setup.");

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            public void onIabSetupFinished(IabResult result) {

                Logger.d("Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Logger.d("Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        InitializeUI();

        loadData();

    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener =
            new IabHelper.QueryInventoryFinishedListener() {

                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                    Logger.d("Query inventory finished.");

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // Is it a failure?
                    if (result.isFailure()) {

                        complain("Failed to query inventory: " + result);

                        return;
                    }

                    Logger.d("Query inventory was successful.");

                    Purchase purchase = inventory.getPurchase(selectedKey);

                    if (purchase != null && verifyDeveloperPayload(purchase)) {

                        Logger.d("We have gas. Consuming it." + selectedKey);

                        mHelper.consumeAsync(inventory.getPurchase(selectedKey),
                                mConsumeFinishedListener);

                        return;
                    }

                    Logger.d("Initial inventory query finished; enabling main UI.");
                }
            };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Logger.d("onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);

        } else {

            Logger.d("onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        Logger.d(msg.toString());

        switch (msg.what) {

            case REQUEST_UPDATE_COIN:

                saveData(selectedKey);

                break;


        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {

        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            Logger.d("Purchase finished: " + result + ", purchase: " + purchase);

            selectedKey = null;

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {

                complain("Error purchasing: " + result);

                setWaitScreen(false);

                return;
            }

            if (!verifyDeveloperPayload(purchase)) {

                complain("Error purchasing. Authenticity verification failed.");

                setWaitScreen(false);

                return;
            }

            Logger.d("Purchase successful.");

            mHelper.consumeAsync(purchase, mConsumeFinishedListener);

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {

                    Logger.d("Consumption finished. Purchase: " + purchase + ", result: " + result);

                    // if we were disposed of in the meantime, quit.
                    if (mHelper == null)
                        return;

                    // We know this is the "gas" sku because it's the only one we consume,
                    // so we don't check which sku was consumed. If you have more than one
                    // sku, you probably should check...
                    if (result.isSuccess()) {
                        // successfully consumed, so we apply the effects of the item in our
                        // game world's logic, which in our case means filling the gas tank a bit

                        sendEmptyMessage(REQUEST_UPDATE_COIN, 1000L);

                    } else {

                        complain("Error while consuming: " + result);

                    }

                    setWaitScreen(false);

                    Logger.d("End consumption flow.");
                }
            };


    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Logger.d("Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonOfferwall:

                showOfferWall();

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Item item = null;

        try {

            item = items.get(position);

        } catch (ArrayIndexOutOfBoundsException e) {

            return;
        }

        selectedKey = item.getKey();

        selectedHeart = Integer.parseInt(item.getHeart());

        String payload = "";

        setWaitScreen(true);

        mHelper.launchPurchaseFlow(this, selectedKey, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void InitializeUI() {

        View footer = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_footer_purchase, null);

        offerwall = (Button) footer.findViewById(R.id.buttonOfferwall);
        offerwall.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);

        listView.addFooterView(footer);

        items = new ArrayList<>();

        itemAdapter = new ItemAdapter(getApplicationContext(), 0, items);

        listView.setAdapter(itemAdapter);

    }

    void complain(String message) {

        Logger.e(message);

        alert("Error: " + message);
    }

    void alert(String message) {

        AlertDialog.Builder bld = new AlertDialog.Builder(this);

        bld.setMessage(message);
        bld.setNeutralButton("OK", null);

        Logger.d("Showing alert dialog: " + message);

        bld.create().show();
    }

    void saveData(String key) {

        showLoading();

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        Logger.d("saveData #1 " + (items == null) + " " + items.size());


        Logger.d("saveData #2");

        final Account account = dbHelper.getAccountInfo();

        SetCoin model = new SetCoin();

        model.setParentID(account.getID());
        model.setCoin(account.getCoin() + selectedHeart);

        Logger.d("saveData #3" );

        SimpleXmlRequest request = HttpHelper.getUpdateCoin(model,
                new Response.Listener<CoinResult>() {

                    @Override
                    public void onResponse(CoinResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                dismissLoading();

                                DBHelper dbHelper = new DBHelper(getApplicationContext());

                                dbHelper.updateCoin(account.getID(), response.getCoin());

                                selectedKey = null;

                                break;

                            default:

                                handleResultCode(response.getResultCode(),
                                        response.getResultMessage());

                                break;
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        handleError(error);
                    }
                });

        addRequest(request);
    }

    private void loadData() {

        showLoading();

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        Account account = dbHelper.getAccountInfo();

        ParentModel parentModel = new ParentModel();

        parentModel.setParentId(account.getID());

        SimpleXmlRequest request = HttpHelper.getGoodItems(parentModel,
                new Response.Listener<GoodsResult>() {

                    @Override
                    public void onResponse(GoodsResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                dismissLoading();

                                List<Item> tmp = response.getItems();

                                if (tmp == null || tmp.size() < 1) {

                                    return;
                                }

                                items.clear();


                                for (Item item : tmp) {

                                    Item good = new Item();

                                    good.setName(AndroidUtils.convertFromUTF8(item.getName()));
                                    good.setInfo(AndroidUtils.convertFromUTF8(item.getInfo()));
                                    good.setCost(item.getCost());
                                    good.setHeart(item.getHeart());
                                    good.setKey(item.getKey());

                                    Logger.d(good.getName() + "\t" + good.getCost() + "\t" + good.getHeart() + "\n" + good.getInfo());

                                    items.add(good);

                                }

                                itemAdapter.notifyDataSetChanged();

                                break;

                            default:

                                handleResultCode(response.getResultCode(), response.getResultMessage());

                                break;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        handleError(error);
                    }
                });

        addRequest(request);

    }

    void setWaitScreen(boolean set) {

        findViewById(R.id.main).setVisibility(set ? View.GONE : View.VISIBLE);
    }

}
