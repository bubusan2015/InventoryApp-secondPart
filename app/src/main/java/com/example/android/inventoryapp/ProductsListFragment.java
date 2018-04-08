package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.adapters.ProductsListCursorAdapter;

import static com.example.android.inventoryapp.data.BookStoreDbContract.*;

public class ProductsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCTS_LOADER_ID = 0;
    private ListView productsListView;
    private ProductsListCursorAdapter productsCursorAdapter;
    private TextView emptyListTextView;
    private FloatingActionButton addBookFab;
    private ProgressBar listProgressBar;

    public ProductsListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productsCursorAdapter = new ProductsListCursorAdapter(getActivity(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products, container, false);
        productsListView = rootView.findViewById(R.id.list_view_products);
        productsListView.setAdapter(productsCursorAdapter);
        emptyListTextView = rootView.findViewById(R.id.empty_list_view);
        listProgressBar = rootView.findViewById(R.id.products_list_progress_bar);

        getActivity().getSupportLoaderManager().initLoader(PRODUCTS_LOADER_ID, null, this);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProductsListFragment.this.getActivity(), BookDetailActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        addBookFab = rootView.findViewById(R.id.fab_add_book);
        addBookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                startActivity(intent);
            }
        });
        productsListView.setEmptyView(emptyListTextView);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PRODUCTS_LOADER_ID:
                String[] projection = {BookEntry._ID, BookEntry.PRODUCT_NAME, BookEntry.QUANTITY, BookEntry.PRICE, BookEntry.SUPPLIER_NAME, BookEntry.SUPPLIER_PHONE};
                return new CursorLoader(getActivity(), BookEntry.CONTENT_URI, projection, null, null, BookEntry.PRODUCT_NAME + " ASC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productsCursorAdapter.swapCursor(data);
        listProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productsCursorAdapter.swapCursor(null);
    }
}