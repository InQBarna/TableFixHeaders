package com.inqbarna.tablefixheaders.adapters;

import android.database.DataSetObserver;
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
	 * An item view type that causes the AdapterView to ignore the item view.
	 * For example, this can be used if the client does not want a particular
	 * view to be given for conversion in
	 * {@link #getView(int, int, View, ViewGroup)}.
	 * 
	 * @see #getItemViewType(int, int)
	 */
	public final static int IGNORE_ITEM_VIEW_TYPE = -1;

	/**
	 * Register an observer that is called when changes happen to the data used
	 * by this adapter.
	 * 
	 * @param observer
	 *            the object that gets notified when the data set changes.
	 */
	void registerDataSetObserver(DataSetObserver observer);

	/**
	 * Unregister an observer that has previously been registered with this
	 * adapter via {@link #registerDataSetObserver}.
	 * 
	 * @param observer
	 *            the object to unregister.
	 */
	void unregisterDataSetObserver(DataSetObserver observer);

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
	public View getView(int row, int column, View convertView, ViewGroup parent);

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

	public int getItemViewType(int row, int column);

	public int getViewTypeCount();

}
