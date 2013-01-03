package com.inqbarna.tablefixheaders;

import java.util.ArrayList;
import java.util.List;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TableFixHeaders extends ViewGroup {
	private final static int CLICK_SENSIVILITY = 2;

	private int currentX;
	private int currentY;

	private TableAdapter adapter;
	private int scrollX;
	private int scrollY;
	private int firstRow;
	private int firstColumn;
	private int[] widths;
	private int[] heights;

	@SuppressWarnings("unused")
	private View headView;
	private List<View> rowViewList;
	private List<View> columnViewList;
	private List<List<View>> bodyViewTable;

	private int rowCount;

	private int columnCount;

	private int width;

	private int height;

	private Recycler recycler;

	public TableFixHeaders(Context context) {
		this(context, null);
	}

	public TableFixHeaders(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.firstRow = 0;
		this.firstColumn = 0;

		this.scrollX = 0;
		this.scrollY = 0;

		this.headView = null;
		this.rowViewList = new ArrayList<View>();
		this.columnViewList = new ArrayList<View>();
		this.bodyViewTable = new ArrayList<List<View>>();
	}

	public TableAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(TableAdapter adapter) {
		this.adapter = adapter;

		this.recycler = new Recycler(adapter.getViewTypeCount());

		this.rowCount = adapter.getRowCount();
		this.columnCount = adapter.getColumnCount();

		widths = new int[columnCount + 1];
		for (int i = -1; i < columnCount; i++) {
			widths[i + 1] += adapter.getWidth(i);
		}
		heights = new int[rowCount + 1];
		for (int i = -1; i < rowCount; i++) {
			heights[i + 1] += adapter.getHeight(i);
		}

		requestLayout();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		boolean intercept = false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				int x2 = currentX - (int) event.getRawX();
				int y2 = currentY - (int) event.getRawY();
				if (x2 < -CLICK_SENSIVILITY || x2 > CLICK_SENSIVILITY || y2 < -CLICK_SENSIVILITY || y2 > CLICK_SENSIVILITY) {
					intercept = true;
				}
				break;
			}
		}
		return intercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				final int x2 = (int) event.getRawX();
				final int y2 = (int) event.getRawY();
				final int diffX = currentX - x2;
				final int diffY = currentY - y2;
				currentX = x2;
				currentY = y2;

				scrollX += diffX;
				scrollY += diffY;
				final boolean up = diffX <= 0;
				final boolean left = diffY <= 0;

				//				System.out.println("up: " + up);
				//				System.out.println("left: " + left);
				// scroll bounds
				if (scrollX == 0) {
					// no op
				} else if (scrollX < 0) {
					scrollX = Math.max(scrollX, -sumArray(widths, 1, firstColumn));
				} else {
					scrollX = Math.min(scrollX, sumArray(widths, firstColumn + 1, columnCount - firstColumn) + widths[0] - width);
				}

				if (scrollY == 0) {
					// no op
				} else if (scrollY < 0) {
					scrollY = Math.max(scrollY, -sumArray(heights, 1, firstRow));
				} else {
					scrollY = Math.min(scrollY, Math.max(0, sumArray(heights, firstRow + 1, rowCount - firstRow) + heights[0] - height));
				}

				// add or remove views
				if (scrollX == 0) {
					// no op
				} else if (!up) {
					while (widths[firstColumn + 1] < scrollX) {
						removeLeft();
						scrollX -= widths[firstColumn + 1];
						firstColumn++;
					}
					while (getFilledWidth() < width) {
						addRight();
					}
				} else {
					while (getFilledWidth() - widths[firstColumn + rowViewList.size() - 1] >= width) {
						removeRight();
					}
					while (0 > scrollX) {
						addLeft();
						firstColumn--;
						scrollX += widths[firstColumn + 1];
					}
				}

				//				System.out.println(scrollY);
				if (scrollY == 0) {
					// no op
				} else if (!left) {
					while (heights[firstRow + 1] < scrollY) {
						removeTop();
						scrollY -= heights[firstRow + 1];
						firstRow++;
					}
					while (getFilledHeight() < height) {
						addBottom();
					}
				} else {
					while (getFilledHeight() - heights[firstRow + columnViewList.size() - 1] >= height) {
						removeBottom();
					}
					while (0 > scrollY) {
						addTop();
						firstRow--;
						scrollY += heights[firstRow + 1];
					}
				}

				repositionViews();
				break;
			}
		}
		return true;
	}

	private int getFilledWidth() {
		return widths[0] + sumArray(widths, firstColumn + 1, rowViewList.size()) - scrollX;
	}

	private int getFilledHeight() {
		return heights[0] + sumArray(heights, firstRow + 1, columnViewList.size()) - scrollY;
	}

	private void addLeft() {
		System.out.println("addLeft");
		addLeftOrRight(firstColumn - 1, 0);
	}

	private void addTop() {
		System.out.println("addTop");
		addTopAndBottom(firstRow - 1, 0);
	}

	private void addRight() {
		System.out.println("addRight");
		final int size = rowViewList.size();
		addLeftOrRight(firstColumn + size, size);
	}

	private void addBottom() {
		System.out.println("addBottom");
		final int size = columnViewList.size();
		addTopAndBottom(firstRow + size, size);
	}

	private void addLeftOrRight(int column, int index) {
		View view = makeView(-1, column, widths[column + 1], heights[0]);
		rowViewList.add(index, view);

		int i = firstRow;
		for (List<View> list : bodyViewTable) {
			view = makeView(i, column, widths[column + 1], heights[i + 1]);
			list.add(index, view);
			i++;
		}
	}

	private void addTopAndBottom(int row, int index) {
		View view = makeView(row, -1, widths[0], heights[row + 1]);
		columnViewList.add(index, view);

		List<View> list = new ArrayList<View>();
		final int size = rowViewList.size() + firstColumn;
		for (int i = firstColumn; i < size; i++) {
			view = makeView(row, i, widths[i + 1], heights[row + 1]);
			list.add(view);
		}
		bodyViewTable.add(index, list);
	}

	private void removeLeft() {
		System.out.println("removeLeft");
		removeLeftOrRight(0);
	}

	private void removeTop() {
		System.out.println("removeTop");
		removeTopOrBottom(0);
	}

	private void removeRight() {
		System.out.println("removeRight");
		removeLeftOrRight(rowViewList.size() - 1);
	}

	private void removeBottom() {
		System.out.println("removeBottom");
		removeTopOrBottom(columnViewList.size() - 1);
	}

	private void removeLeftOrRight(int position) {
		removeView(rowViewList.remove(position));
		for (List<View> list : bodyViewTable) {
			removeView(list.remove(position));
		}
	}

	private void removeTopOrBottom(int position) {
		removeView(columnViewList.remove(position));
		List<View> remove = bodyViewTable.remove(position);
		for (View view : remove) {
			removeView(view);
		}
	}

	@Override
	public void removeView(View view) {
		super.removeView(view);

		recycler.addRecycledView(view, 0);
	}

	private void repositionViews() {
		int left, top, right, bottom, i;

		left = widths[0] - scrollX;
		i = firstColumn;
		for (View view : rowViewList) {
			right = left + widths[++i];
			view.layout(left, 0, right, heights[0]);
			left = right;
		}

		top = heights[0] - scrollY;
		i = firstRow;
		for (View view : columnViewList) {
			bottom = top + heights[++i];
			view.layout(0, top, widths[0], bottom);
			top = bottom;
		}

		top = heights[0] - scrollY;
		i = firstRow;
		for (List<View> list : bodyViewTable) {
			bottom = top + heights[++i];
			left = widths[0] - scrollX;
			int j = firstColumn;
			for (View view : list) {
				right = left + widths[++j];
				view.layout(left, top, right, bottom);
				left = right;
			}
			top = bottom;
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		final int w;
		final int h;

		if (adapter != null) {
			if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				w = Math.min(widthSize, sumArray(widths));
			} else {
				w = widthSize;
			}

			if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				h = Math.min(heightSize, sumArray(heights));
			} else {
				h = heightSize;
			}
		} else {
			if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				w = 0;
				h = 0;
			} else {
				w = widthSize;
				h = heightSize;
			}
		}

		setMeasuredDimension(w, h);
	}

	private int sumArray(int array[]) {
		return sumArray(array, 0, array.length);
	}

	private int sumArray(int array[], int firstIndex, int count) {
		int sum = 0;
		count += firstIndex;
		for (int i = firstIndex; i < count; i++) {
			sum += array[i];
		}
		return sum;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("boolean " + changed + ", int " + l + ", int " + t + ", int " + r + ", int " + b);

		if (changed && adapter != null) {
			resetTable();

			width = r - l;
			height = b - t;

			int left, top, right, bottom;

			headView = makeAndSetup(-1, -1, 0, 0, widths[0], heights[0]);

			left = widths[0] - scrollX;
			for (int i = firstColumn; i < columnCount && left < width; i++) {
				right = left + widths[i + 1];
				final View view = makeAndSetup(-1, i, left, 0, right, heights[0]);
				rowViewList.add(view);
				left = right;
			}

			top = heights[0] - scrollY;
			for (int i = firstRow; i < rowCount && top < height; i++) {
				bottom = top + heights[i + 1];
				final View view = makeAndSetup(i, -1, 0, top, widths[0], bottom);
				columnViewList.add(view);
				top = bottom;
			}

			top = heights[0] - scrollY;
			for (int i = firstRow; i < rowCount && top < height; i++) {
				bottom = top + heights[i + 1];
				left = widths[0] - scrollX;
				List<View> list = new ArrayList<View>();
				for (int j = firstColumn; j < columnCount && left < width; j++) {
					right = left + widths[j + 1];
					final View view = makeAndSetup(i, j, left, top, right, bottom);
					list.add(view);
					left = right;
				}
				bodyViewTable.add(list);
				top = bottom;
			}
		}
	}

	private void resetTable() {
		headView = null;
		rowViewList.clear();
		columnViewList.clear();
		bodyViewTable.clear();

		removeAllViews();
	}

	private View makeAndSetup(int row, int column, int left, int top, int right, int bottom) {
		final View view = makeView(row, column, right - left, bottom - top);
		view.layout(left, top, right, bottom);
		return view;
	}

	private View makeView(int row, int column, int w, int h) {
		final View view = adapter.getView(row, column, recycler.getRecycledView(adapter.getItemViewType(row, column)), this);
		view.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
		addTableView(view, row, column);
		return view;
	}

	private void addTableView(View view, int row, int column) {
		if (row == -1 && column == -1) {
			addView(view);
		} else if (row == -1 || column == -1) {
			addView(view, getChildCount() - 1);
		} else {
			addView(view, 0);
		}
	}
}
