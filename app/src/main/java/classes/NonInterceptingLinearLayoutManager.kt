package classes

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NonInterceptingLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        // Similarly, you can customize this to check if your RecyclerView is actually scrollable before deciding whether to intercept touch events.
        return findFirstVisibleItemPosition() > 0 || findLastVisibleItemPosition() < itemCount - 1
    }
}