package com.careons.app.Patient.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Adapter<X,Y> extends RecyclerView.Adapter<ObjectHolder<X,Y>> {

    private ArrayList<X> items;
    private int placeholder;
    private int case_constant;

    public Adapter(ArrayList<X> data, int _placeholder, int _case) {
        super();
        items = data;
        placeholder = _placeholder;
        case_constant = _case;
    }

    @Override
    public ObjectHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewDataBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                placeholder, viewGroup, false);

        return new ObjectHolder(binding.getRoot());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public void onBindViewHolder(ObjectHolder objectHolder, int position) {

        ViewDataBinding binding = objectHolder.getBinding();
        objectHolder.bindConnection(items.get(position), binding, case_constant);
    }
}
