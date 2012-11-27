package com.inqbarna.tablefixheaders.samples.adapters;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MatrixTableAdapter<T> implements TableAdapter {

	private final static int WIDTH_DIP = 110;
	private final static int HEIGHT_DIP = 45;

	private final Context context;

	private T[][] table;

	private final int width;
	private final int height;

	public MatrixTableAdapter(Context context) {
		this(context, null);
	}

	public MatrixTableAdapter(Context context, T[][] table) {
		this.context = context;
		Resources r = context.getResources();

		width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
		height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_DIP, r.getDisplayMetrics()));

		setInformation(table);
	}

	public void setInformation(T[][] table) {
		this.table = table;
	}

	@Override
	public int getRowCount() {
		return table.length - 1;
	}

	@Override
	public int getColumnCount() {
		return table[0].length - 1;
	}

	@Override
	public View getView(int row, int column, ViewGroup parent) {
		return generateView(table[row + 1][column + 1]);
	}

	private TextView generateView(T item) {
		final TextView textView = new TextView(context);
		textView.setText(item.toString());
		return textView;
	}

	@Override
	public int getHeight(int row) {
		return height;
	}

	@Override
	public int getWidth(int column) {
		return width;
	}
}
