package com.inqbarna.tablefixheaders.samples;

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.inqbarna.tablefixheaders.samples.adapters.MatrixTableAdapter;

import android.app.Activity;
import android.os.Bundle;

public class SimpleTable extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table);

		TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);
		MatrixTableAdapter<String> matrixTableAdapter = new MatrixTableAdapter<String>(this, new String[][] {
				{
						"Header 1",
						"Header 2",
						"Header 3",
						"Header 4",
						"Header 5",
						"Header 6" },
				{
						"Lorem",
						"sed",
						"do",
						"eiusmod",
						"tempor",
						"incididunt" },
				{
						"ipsum",
						"irure",
						"occaecat",
						"enim",
						"laborum",
						"reprehenderit" },
				{
						"dolor",
						"fugiat",
						"nulla",
						"reprehenderit",
						"laborum",
						"consequat" },
				{
						"sit",
						"consequat",
						"laborum",
						"fugiat",
						"eiusmod",
						"enim" },
				{
						"amet",
						"nulla",
						"Excepteur",
						"voluptate",
						"occaecat",
						"et" },
				{
						"consectetur",
						"occaecat",
						"fugiat",
						"dolore",
						"consequat",
						"eiusmod" },
				{
						"adipisicing",
						"fugiat",
						"Excepteur",
						"occaecat",
						"fugiat",
						"laborum" },
				{
						"elit",
						"voluptate",
						"reprehenderit",
						"Excepteur",
						"fugiat",
						"nulla" },
		});
		tableFixHeaders.setAdapter(matrixTableAdapter);
	}
}
