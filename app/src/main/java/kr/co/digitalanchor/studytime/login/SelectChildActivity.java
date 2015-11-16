package kr.co.digitalanchor.studytime.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import java.util.ArrayList;
import java.util.List;
import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.model.Child;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-11-10.
 */
public class SelectChildActivity extends BaseActivity implements AdapterView.OnItemClickListener,
    View.OnClickListener {

  ListView mList;

  View mHeader;

  View mFooter;

  ChildListAdapter mAdapter;

  List<Child> mChildren;

  String parentId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_child_select);

    Bundle data = getIntent().getExtras();

    if (data != null) {

      parentId = data.getString("ParentID");

    }

    initView();


  }

  @Override
  protected void onStart() {
    super.onStart();

    requestChildList();
  }

  public void initView() {

    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

    mHeader = inflater.inflate(R.layout.layout_child_header, null);
    mFooter = inflater.inflate(R.layout.layout_child_footer_c, null);
    mFooter.setOnClickListener(this);

    mList = (ListView) findViewById(R.id.list);

    mList.setOnItemClickListener(this);

    mList.addHeaderView(mHeader);
    mList.addFooterView(mFooter);

    mList.setAdapter(makeAdapter());

  }

  private ChildListAdapter makeAdapter() {

    if (mChildren == null) {

      mChildren = new ArrayList<>();
    }

    mAdapter = new ChildListAdapter(getApplicationContext(), 0, mChildren);

    return mAdapter;
  }


  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    if (position < 1) {

      return;
    }

    Toast.makeText(getApplicationContext(), "Click item " + position, Toast.LENGTH_SHORT).show();

    // TODO complete login
  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {

      case R.id.footerSendLink:

        Toast.makeText(getApplicationContext(), "Click footer", Toast.LENGTH_SHORT).show();

        setResult(INPUT_ADD_INFO);

        finish();

        break;
    }
  }

  private void requestChildList() {

    showLoading();

    ParentModel model = new ParentModel();

    model.setParentId(parentId);

    SimpleXmlRequest request = HttpHelper.getSyncParentData(model,
        new Response.Listener<ParentLoginResult>() {
          @Override
          public void onResponse(ParentLoginResult res) {

            dismissLoading();

            switch (res.getResultCode()) {

              case SUCCESS:

                List<Child> list = res.getChildren();

                if (list != null) {

                  for (Child child : list) {

                    child.setName(AndroidUtils.convertFromUTF8(child.getName()));

                    mChildren.add(child);
                  }

                  mAdapter.notifyDataSetChanged();
                }

                break;

              default:

                handleResultCode(res.getResultCode(), res.getResultMessage());

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
}
