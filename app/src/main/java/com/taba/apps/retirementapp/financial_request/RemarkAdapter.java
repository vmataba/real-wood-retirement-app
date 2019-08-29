package com.taba.apps.retirementapp.financial_request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taba.apps.retirementapp.R;
import com.taba.apps.retirementapp.extra.Tool;

import java.util.ArrayList;

public class RemarkAdapter extends RecyclerView.Adapter<RemarkAdapter.ViewHolder>{

    private ArrayList<Remark> remarks;
    private Context context;

    public RemarkAdapter(Context context, ArrayList<Remark> remarks){
        this.context = context;
        this.remarks = remarks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View singleRemarkView = LayoutInflater.from(this.context).inflate(R.layout._request_remark,viewGroup,false);
        RemarkAdapter.ViewHolder viewHolder = new RemarkAdapter.ViewHolder(singleRemarkView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Remark remark = this.remarks.get(position);


        Picasso.with(context).load(remark.getUserPhoto()).fit().transform(Tool.getImageTransformation()).into(viewHolder.userImage);
        viewHolder.userFullName.setText(remark.getUserFullName());
        viewHolder.userTitle.setText(remark.getUserTitle());
        viewHolder.requestRemark.setText(remark.getRemark());
        viewHolder.createdAt.setText(remark.getCreatedAt());
        viewHolder.requestStatus.setText(remark.getNamedStatus());
        viewHolder.requestStatus.setTextColor(context.getResources().getColor(remark.getStatusColor()));

    }

    @Override
    public int getItemCount() {
        return this.remarks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View view;
        public ImageView userImage;
        private TextView userFullName;
        private TextView userTitle;
        private TextView requestStatus;
        private TextView requestRemark;
        private TextView createdAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;

            this.userImage = this.view.findViewById(R.id.userImage);
            this.userFullName = this.view.findViewById(R.id.userFullName);
            this.userTitle = this.view.findViewById(R.id.userTitle);
            this.requestStatus = this.view.findViewById(R.id.requestStatus);
            this.requestRemark = this.view.findViewById(R.id.userRemarks);
            this.createdAt = this.view.findViewById(R.id.createdAt);
        }
    }

}
