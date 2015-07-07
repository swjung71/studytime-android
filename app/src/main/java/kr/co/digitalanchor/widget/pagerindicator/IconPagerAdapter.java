package kr.co.digitalanchor.widget.pagerindicator;

/**
 * Created by Thomas on 2015-07-06.
 */
public interface IconPagerAdapter {

    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
