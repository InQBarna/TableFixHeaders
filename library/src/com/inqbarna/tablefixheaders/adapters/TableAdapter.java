package com.inqbarna.tablefixheaders.adapters;

import com.inqbarna.tablefixheaders.TableFixHeaders;

import android.view.View;
import android.view.ViewGroup;

/**
 * The TableAdapter object acts as a bridge between an TableFixHeaders and the
 * underlying data for that view. The Adapter provides access to the data items.
 * The Adapter is also responsible for making a View for each item in the data
 * set.
 * 
 * @author Brais Gabín
 * @see TableFixHeaders
 */
public interface TableAdapter {

	/**
	 * How many rows are in the data table represented by this Adapter.
	 * 
	 * @return count of rows.
	 */
	public int getRowCount();

	/**
	 * How many columns are in the data table represented by this Adapter.
	 * 
	 * @return count of columns.
	 */
	public int getColumnCount();

	/**
	 * Get a View that displays the data at the specified row and column in the
	 * data table. You can either create a View manually or inflate it from an
	 * XML layout file.
	 * 
	 * @param row
	 *            The row of the item within the adapter's data table of the
	 *            item whose view we want. If the row is <code>-1</code> it is
	 *            the header.
	 * @param column
	 *            The column of the item within the adapter's data table of the
	 *            item whose view we want. If the column is <code>-1</code> it
	 *            is the header.
	 * @param parent
	 *            The parent that this view will eventually be attached to.
	 * @return A View corresponding to the data at the specified row and column.
	 */
	public View getView(int row, int column, ViewGroup parent);

	/**
	 * Return the width of the column.
	 * 
	 * @param column
	 *            the column. If the column is <code>-1</code> it is the header.
	 * @return The width of the column, in pixels.
	 */
	public int getWidth(int column);

	/**
	 * Return the height of the row.
	 * 
	 * @param row
	 *            the row. If the row is <code>-1</code> it is the header.
	 * @return The height of the row, in pixels.
	 */
	public int getHeight(int row);
}
