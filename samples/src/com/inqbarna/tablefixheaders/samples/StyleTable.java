package com.inqbarna.tablefixheaders.samples;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.inqbarna.tablefixheaders.TableFixHeaders2;
import com.inqbarna.tablefixheaders.samples.adapters.BaseTableAdapter;

public class StyleTable extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table);

		TableFixHeaders2 tableFixHeaders = (TableFixHeaders2) findViewById(R.id.table);
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
			return 3;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getWidth(int column) {
			return width;
		}

		@Override
		public int getHeight(int row) {
			return height;
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
	}
}
