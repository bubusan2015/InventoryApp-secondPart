package com.example.android.inventoryapp;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookStoreDbContract;


public class BriefingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BRIEFING_LOADER_ID = 1;
    private TextView numberOfBooks;
    private TextView numberOfSuppliers;

    public BriefingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_briefing, container, false);
        numberOfBooks = rootView.findViewById(R.id.tv_briefing_number_of_books);
        numberOfSuppliers = rootView.findViewById(R.id.tv_briefing_number_of_suppliers);
        getActivity().getSupportLoaderManager().initLoader(BRIEFING_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BRIEFING_LOADER_ID:
                return new CursorLoader(getActivity(), BookStoreDbContract.BookEntry.CONTENT_BRIEF_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case BRIEFING_LOADER_ID:
                data.moveToFirst();
                numberOfBooks.setText(String.valueOf(data.getInt(data.getColumnIndex(BookStoreDbContract.BookEntry.QUANTITY))));
                numberOfSuppliers.setText(String.valueOf(data.getInt(data.getColumnIndex(BookStoreDbContract.BookEntry.DISTINCT_SUPPLIERS))));
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
