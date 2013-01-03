package com.inqbarna.tablefixheaders.samples;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.inqbarna.tablefixheaders.samples.adapters.BaseTableAdapter;

public class StyleTable extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table);

		TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);
		tableFixHeaders.setAdapter(new MyAdapter(this));
	}

	public class MyAdapter extends BaseTableAdapter {

		private final int width;
		private final int height;

		public MyAdapter(Context context) {
			super(context);

			Resources resources = context.getResources();

			width = resources.getDimensionPixelSize(R.dimen.table_width);
			height = resources.getDimensionPixelSize(R.dimen.table_height);
		}

		@Override
		public int getRowCount() {
			return 10;
		}

		@Override
		public int getColumnCount() {
			return 8;
		}

		@Override
		public int getWidth(int column) {
			return column == 1 ? width / 2 : width;
		}

		@Override
		public int getHeight(int row) {
			return row == 1 ? height / 2 : height;
		}

		@Override
		public String getCellString(int row, int column) {
			return "Lorem (" + row + ", " + column + ")";
		}

		@Override
		public int getLayoutResource(int row, int column) {
			if (row < 0) {
				return R.layout.item_table1_header;
			} else {
				return R.layout.item_table1;
			}
		}

		@Override
		public int getItemViewType(int row, int column) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}
	}
}
