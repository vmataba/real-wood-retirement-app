package com.taba.apps.retirementapp.financial_request;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taba.apps.retirementapp.R;
import com.taba.apps.retirementapp.SingleRequestActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FinancialRequestAdapter extends RecyclerView.Adapter<FinancialRequestAdapter.ViewHolder> {

    private ArrayList<FinancialRequest> financialRequests;
    private Context context;


    public FinancialRequestAdapter(Context context, ArrayList<FinancialRequest> financialRequests) {
        this.context = context;
        this.financialRequests = financialRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View singleRequestView = LayoutInflater.from(this.context).inflate(R.layout._financial_request, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(singleRequestView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        final FinancialRequest request = this.financialRequests.get(position);

        viewHolder.requestStatus.setText(request.getNamedStatus());
        viewHolder.requestStatus.setTextColor(context.getResources().getColor(request.getStatusColor()));
        viewHolder.requestAmount.setText(request.getTextAmount() + "/=");

        if (request.isApproved() && request.hasAmountDifference()) {
            viewHolder.requestAmount.setPaintFlags(viewHolder.requestAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        viewHolder.issuedAmount.setText(Html.fromHtml(request.getTextIssuedAmount() + "/="));
        viewHolder.requestRemarks.setText(request.getRequestRemarks());

        if (request.getUpdatedAt().equals("")){
            viewHolder.requestRemarks.setText(request.getRequestedAt());
        }

        viewHolder.updatedAt.setText(request.getUpdatedAt());

        viewHolder.requestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleRequestActivity.class);
                intent.putExtra("request", request);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.financialRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public CardView requestCard;
        public TextView requestStatus;
        public TextView requestAmount;
        public TextView issuedAmount;
        public TextView requestRemarks;
        public TextView updatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;

            this.requestCard = this.view.findViewById(R.id.requestCard);
            this.requestStatus = this.view.findViewById(R.id.requestStatus);
            this.requestAmount = this.view.findViewById(R.id.requestAmount);
            this.issuedAmount = this.view.findViewById(R.id.issuedAmount);
            this.requestRemarks = this.view.findViewById(R.id.requestRemarks);
            this.updatedAt = this.view.findViewById(R.id.dateUpdated);
        }
    }

}
