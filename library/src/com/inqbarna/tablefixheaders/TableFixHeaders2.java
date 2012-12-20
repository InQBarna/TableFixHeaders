package com.inqbarna.tablefixheaders;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TableFixHeaders2 extends ViewGroup {

	private TableAdapter adapter;

	public TableFixHeaders2(Context context) {
		super(context);
	}

	public TableFixHeaders2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TableAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(TableAdapter adapter) {
		this.adapter = adapter;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		final int w;
		final int h;

		if (adapter != null) {
			if (widthMode == MeasureSpec.AT_MOST) {
				w = Math.min(widthSize, getAdapterDesiredWidth());
			} else {
				w = widthSize;
			}

			if (heightMode == MeasureSpec.AT_MOST) {
				h = Math.min(heightSize, getAdapterDesiredHeight());
			} else {
				h = heightSize;
			}
		} else {
			w = widthSize;
			h = heightSize;
		}

		setMeasuredDimension(w, h);
	}

	private int getAdapterDesiredWidth() {
		final int columnCount = adapter.getColumnCount();
		int desired = 0;
		for (int i = -1; i < columnCount; i++) {
			desired += adapter.getWidth(i);
		}
		return desired;
	}

	private int getAdapterDesiredHeight() {
		final int rowCount = adapter.getRowCount();
		int desired = 0;
		for (int i = -1; i < rowCount; i++) {
			desired += adapter.getHeight(i);
		}
		return desired;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("boolean " + changed + ", int " + l + ", int " + t + ", int " + r + ", int " + b);

		removeAllViews();
		final int rowCount = adapter.getRowCount();
		final int columnCount = adapter.getColumnCount();

		final int[] widths = getAdapterWidths();
		final int[] heights = getAdapterHeights();
		int h = 0;
		int nextH;
		for (int i = -1; i < rowCount; i++) {
			nextH = h + heights[i + 1];
			int w = 0;
			int nextW;
			for (int j = -1; j < columnCount; j++) {
				View view = adapter.getView(i, j, this);
				view.measure(MeasureSpec.makeMeasureSpec(widths[j + 1], MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heights[i + 1], MeasureSpec.EXACTLY));
				addView(view);
				nextW = w + widths[j + 1];
				view.layout(w, h, nextW, nextH);
				w = nextW;
			}
			h = nextH;
		}
	}

	private int[] getAdapterWidths() {
		final int columnCount = adapter.getColumnCount();
		int array[] = new int[columnCount + 1];
		for (int i = -1; i < columnCount; i++) {
			array[i + 1] += adapter.getWidth(i);
		}
		return array;
	}

	private int[] getAdapterHeights() {
		final int rowCount = adapter.getRowCount();
		int array[] = new int[rowCount + 1];
		for (int i = -1; i < rowCount; i++) {
			array[i + 1] += adapter.getHeight(i);
		}
		return array;
	}
}
