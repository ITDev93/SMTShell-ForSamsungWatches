package com.samsung.SMT.lang.smtshellwatch.watch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsung.SMT.lang.smtshellwatch.R;

import java.util.ArrayList;

public class WearableRecycleViewAdapter extends RecyclerView.Adapter<WearableRecycleViewAdapter.SMTCapabilityHolder> {

    private final ArrayList<SMTCapability> dataSource;

    public WearableRecycleViewAdapter(ArrayList<SMTCapability> dataArgs){
        this.dataSource = dataArgs;
    }

    @NonNull
    @Override
    public SMTCapabilityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.descriptive_button,parent,false);

        return new SMTCapabilityHolder(view);
    }

    public static class SMTCapabilityHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView desc;
        Button btn;

        public SMTCapabilityHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.title);
            this.desc = view.findViewById(R.id.details);
            this.btn = view.findViewById(R.id.btn);
        }
    }

    @Override
    public void onBindViewHolder(SMTCapabilityHolder holder,  int position) {
        SMTCapability data_provider = dataSource.get(position);

        holder.title.setText(data_provider.getTitle());
        holder.desc.setText(data_provider.getDesc());
        holder.btn.setText(data_provider.getBtnText());
        holder.btn.setOnClickListener(data_provider.getOnClick());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
