package com.alphawallet.app.ui.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.AirDrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ruffian.library.widget.RTextView;

import java.util.List;

public class AirDropAdapter extends BaseQuickAdapter<AirDrop, BaseViewHolder>
{
    public AirDropAdapter()
    {
        super(R.layout.item_airdop_view);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, AirDrop airDrop)
    {
        baseViewHolder.setText(R.id.tv_name, "TG:" + airDrop.tg_user_id);
        baseViewHolder.setText(R.id.tv_value, "数量：" + airDrop.amount + "");
        RTextView button = baseViewHolder.getView(R.id.tv_btn);
        if (airDrop.drop)
        {
            button.getHelper().setBackgroundColorNormal(getContext().getResources().getColor(R.color.mike));
            button.setText("已投");
        }
        else
        {
            button.getHelper().setBackgroundColorNormal(getContext().getResources().getColor(R.color.green));
            button.setText("投币");
        }
    }

    public void setDrop(String tgId)
    {
        List<AirDrop> list = getData();
        for (AirDrop item : list)
        {
            if (tgId.equals(item.tg_user_id))
            {
                item.drop = true;
            }
        }
        notifyDataSetChanged();
    }
}
