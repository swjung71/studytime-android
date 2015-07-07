package kr.co.digitalanchor.studytime.intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-07-06.
 */
public class GuideFragment extends Fragment {

    private static final String KEY_CONTENT = "GuideFragment:Content";

    public static GuideFragment newInstance(int content) {

        GuideFragment fragment = new GuideFragment();

        fragment.mContent = content;

        return fragment;
    }

    private int mContent = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        if ((savedInstanceState != null)
//                && savedInstanceState.containsKey(KEY_CONTENT)) {
//
//            mContent = savedInstanceState.getInt(KEY_CONTENT);
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        outState.putInt(KEY_CONTENT, mContent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        imageView.setImageResource(mContent);

        return view;
    }
}
