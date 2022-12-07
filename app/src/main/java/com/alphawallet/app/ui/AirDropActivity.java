package com.alphawallet.app.ui;

import static com.alphawallet.app.C.Key.WALLET;
import static com.alphawallet.ethereum.EthereumNetworkBase.MAINNET_ID;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphawallet.app.BuildConfig;
import com.alphawallet.app.C;
import com.alphawallet.app.R;
import com.alphawallet.app.entity.AirDrop;
import com.alphawallet.app.entity.AirDropBase;
import com.alphawallet.app.entity.QRResult;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.event.AirDropEvent;
import com.alphawallet.app.router.SendTokenRouter;
import com.alphawallet.app.ui.adapter.AirDropAdapter;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.Call;

@AndroidEntryPoint
public class AirDropActivity extends BaseActivity
{
    private RecyclerView airRv;
    private AirDropAdapter airDropAdapter;
    private Wallet wallet;
    private ProgressDialog _progressDialog;
    private String dropAddress;
    private String dropTGId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airdrop);
        EventBus.getDefault().register(this);
        toolbar();
        setTitle("空投");
        wallet = getIntent().getParcelableExtra(C.Key.WALLET);

        initView();
        loadData();
    }

    @Override
    protected void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView()
    {
        if (_progressDialog == null)
        {
            _progressDialog = new ProgressDialog(this);
            _progressDialog.setCancelable(true);
        }
        airRv = findViewById(R.id.air_rv);
        airRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        airRv.setAdapter(airDropAdapter = new AirDropAdapter());
        airRv.addItemDecoration(new RecyclerView.ItemDecoration()
        {
            int spacing = 3;

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
            {
                outRect.bottom = spacing;
            }
        });
        airDropAdapter.setOnItemClickListener((adapter, view, position) -> {
            AirDrop airDrop = airDropAdapter.getItem(position);
            if (airDrop.drop) return;
            dropAddress = airDrop.address;
            dropTGId = airDrop.tg_user_id;
            new SendTokenRouter().openTx(this, wallet.address, airDrop.address, airDrop.amount, "ROSE", 18, wallet, 42262);
        });
    }

    private void loadData()
    {
        _progressDialog.setMessage("loading");
        _progressDialog.show();
        String url =  "/api/v1/airdrop/address";
        OkHttpUtils.post().url(url).build().execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                _progressDialog.dismiss();
                Log.d("TTT", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id)
            {
                _progressDialog.dismiss();
                AirDropBase base = new Gson().fromJson(response, AirDropBase.class);
                airDropAdapter.setList(base.data);
            }
        });
    }

    private void postDropData(String txHash)
    {
        String url = "/v1/airdrop/callback";
        Map<String, String> params = new HashMap();
        params.put("address", dropAddress);
        params.put("tg_user_id", dropTGId);
        params.put("tx_hash", txHash);
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }

            @Override
            public void onResponse(String response, int id)
            {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(AirDropEvent event)
    {
        Toast.makeText(this, "空投成功！", Toast.LENGTH_SHORT).show();
        airDropAdapter.setDrop(dropTGId);
        postDropData(event.txHash);
    }

}
