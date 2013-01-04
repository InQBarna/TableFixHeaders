package com.inqbarna.tablefixheaders;

import java.util.ArrayList;
import java.util.List;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * This view shows a table which can scroll in both directions. Also still
 * leaves the headers fixed.
 * 
 * @author Brais Gabín (InQBarna)
 */
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

	private TableAdapterDataSetObserver tableAdapterDataSetObserver;
	private boolean needRelayout;

	private final ImageView[] shadows;
	private final int shadowSize;

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 */
	public TableFixHeaders(Context context) {
		this(context, null);
	}

	/**
	 * Constructor that is called when inflating a view from XML. This is called
	 * when a view is being constructed from an XML file, supplying attributes
	 * that were specified in the XML file. This version uses a default style of
	 * 0, so the only attribute values applied are those in the Context's Theme
	 * and the given AttributeSet.
	 * 
	 * The method onFinishInflate() will be called after all children have been
	 * added.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 */
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

		this.needRelayout = true;

		this.shadows = new ImageView[4];
		this.shadows[0] = new ImageView(context);
		this.shadows[0].setImageResource(R.drawable.shadow_left);
		this.shadows[1] = new ImageView(context);
		this.shadows[1].setImageResource(R.drawable.shadow_top);
		this.shadows[2] = new ImageView(context);
		this.shadows[2].setImageResource(R.drawable.shadow_right);
		this.shadows[3] = new ImageView(context);
		this.shadows[3].setImageResource(R.drawable.shadow_bottom);

		shadowSize = getResources().getDimensionPixelSize(R.dimen.shadow_size);
	}

	/**
	 * Returns the adapter currently associated with this widget.
	 * 
	 * @return The adapter used to provide this view's content.
	 */
	public TableAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets the data behind this TableFixHeaders.
	 * 
	 * @param adapter
	 *            The TableAdapter which is responsible for maintaining the data
	 *            backing this list and for producing a view to represent an
	 *            item in that data set.
	 */
	public void setAdapter(TableAdapter adapter) {
		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(tableAdapterDataSetObserver);
		}

		this.adapter = adapter;
		tableAdapterDataSetObserver = new TableAdapterDataSetObserver();
		this.adapter.registerDataSetObserver(tableAdapterDataSetObserver);

		this.recycler = new Recycler(adapter.getViewTypeCount());

		this.rowCount = adapter.getRowCount();
		this.columnCount = adapter.getColumnCount();

		needRelayout = true;
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
				final Boolean left = diffX == 0 ? null : diffX <= 0;
				final Boolean up = diffY == 0 ? null : diffY <= 0;

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
				if (left == null) {
					// no op
				} else if (!left && scrollX != 0) {
					while (widths[firstColumn + 1] < scrollX) {
						removeLeft();
						scrollX -= widths[firstColumn + 1];
						firstColumn++;
					}
					while (getFilledWidth() < width) {
						addRight();
					}
				} else if (left) {
					while (getFilledWidth() - widths[firstColumn + rowViewList.size() - 1] >= width) {
						removeRight();
					}
					while (0 > scrollX) {
						addLeft();
						firstColumn--;
						scrollX += widths[firstColumn + 1];
					}
				}

				if (up == null) {
					// no op
				} else if (!up && scrollY != 0) {
					while (heights[firstRow + 1] < scrollY) {
						removeTop();
						scrollY -= heights[firstRow + 1];
						firstRow++;
					}
					while (getFilledHeight() < height) {
						addBottom();
					}
				} else if (up) {
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

				shadowsVisibility();
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
		System.out.println(row + ", " + heights.length);
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

		final int typeView = (Integer) view.getTag(R.id.tag_type_view);
		if (typeView != TableAdapter.IGNORE_ITEM_VIEW_TYPE) {
			recycler.addRecycledView(view, typeView);
		}
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
			widths = new int[columnCount + 1];
			for (int i = -1; i < columnCount; i++) {
				widths[i + 1] += adapter.getWidth(i);
			}
			heights = new int[rowCount + 1];
			for (int i = -1; i < rowCount; i++) {
				heights[i + 1] += adapter.getHeight(i);
			}

			if (widthMode == MeasureSpec.AT_MOST) {
				w = Math.min(widthSize, sumArray(widths));
			} else if (widthMode == MeasureSpec.UNSPECIFIED) {
				w = sumArray(widths);
			} else {
				w = widthSize;
				int sumArray = sumArray(widths);
				if (sumArray < widthSize) {
					final float factor = widthSize / (float) sumArray;
					for (int i = 1; i < widths.length; i++) {
						widths[i] = Math.round(widths[i] * factor);
					}
					widths[0] = widthSize - sumArray(widths, 1, widths.length - 1);
				}
			}

			if (heightMode == MeasureSpec.AT_MOST) {
				h = Math.min(heightSize, sumArray(heights));
			} else if (heightMode == MeasureSpec.UNSPECIFIED) {
				h = sumArray(heights);
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

		if (needRelayout || changed) {
			needRelayout = false;
			resetTable();

			if (adapter != null) {
				width = r - l;
				height = b - t;

				int left, top, right, bottom;

				right = Math.min(width, sumArray(widths));
				bottom = Math.min(height, sumArray(heights));
				addShadow(shadows[0], widths[0], 0, widths[0] + shadowSize, bottom);
				addShadow(shadows[1], 0, heights[0], right, heights[0] + shadowSize);
				addShadow(shadows[2], right - shadowSize, 0, right, bottom);
				addShadow(shadows[3], 0, bottom - shadowSize, right, bottom);

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

				shadowsVisibility();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void shadowsVisibility() {
		final int[] remainPixels = {
				scrollX + sumArray(widths, 1, firstColumn),
				scrollY + sumArray(heights, 1, firstRow),
				-scrollX + (sumArray(widths, firstColumn + 1, columnCount - firstColumn) + widths[0] - width),
				-scrollY + Math.max(0, sumArray(heights, firstRow + 1, rowCount - firstRow) + heights[0] - height),
		};

		for (int i = 0; i < shadows.length; i++) {
			shadows[i].setAlpha(Math.round((remainPixels[i] < shadowSize ? (remainPixels[i] * 255) / (float) shadowSize : 255)));
		}
	}

	private void addShadow(ImageView imageView, int l, int t, int r, int b) {
		imageView.layout(l, t, r, b);
		addView(imageView);
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
		final int itemViewType = adapter.getItemViewType(row, column);
		final View recycledView;
		if (itemViewType == TableAdapter.IGNORE_ITEM_VIEW_TYPE) {
			recycledView = null;
		} else {
			recycledView = recycler.getRecycledView(itemViewType);
		}
		final View view = adapter.getView(row, column, recycledView, this);
		view.setTag(R.id.tag_type_view, itemViewType);
		view.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
		addTableView(view, row, column);
		return view;
	}

	private void addTableView(View view, int row, int column) {
		if (row == -1 && column == -1) {
			addView(view, getChildCount() - 4);
		} else if (row == -1 || column == -1) {
			addView(view, getChildCount() - 5);
		} else {
			addView(view, 0);
		}
	}

	private class TableAdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			needRelayout = true;
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			// Do nothing
		}
	}
}
