package se7kn8.servercontroller.app.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {

	public interface RecyclerViewClickListener {
		void onItemClick(View view, int position);

		void onItemLongClick(View view, int position);
	}

	private GestureDetector detector;
	private RecyclerViewClickListener clickListener;

	public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, RecyclerViewClickListener listener) {
		this.clickListener = listener;
		detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
				if(child != null && clickListener != null){
					clickListener.onItemLongClick(child, recyclerView.getChildAdapterPosition(child));
				}
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
		View child = view.findChildViewUnder(e.getX(), e.getY());
		if(child != null && clickListener != null && detector.onTouchEvent(e)){
			clickListener.onItemClick(child, view.getChildAdapterPosition(child));
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {

	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

	}
}
