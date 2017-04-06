package com.lmq.main.activity.investmanager.reward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.util.Default;

public class RewardManagerActivity extends BaseActivity implements OnClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_management_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.reward_title);


		findViewById(R.id.back).setOnClickListener(this);
		if(Default.IS_YB || Default.IS_ZFT){
			findViewById(R.id.rl_reward_reward_redpackets).setVisibility(View.GONE);
		}else{
			findViewById(R.id.rl_reward_reward_redpackets).setOnClickListener(this);;
		}
		findViewById(R.id.rl_reward_integration).setOnClickListener(this);
		findViewById(R.id.rl_reward_invitation).setOnClickListener(this);
		findViewById(R.id.rl_reward_coupon).setOnClickListener(this);

	}

	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;

			case R.id.rl_reward_reward_redpackets:
				//TODO 我的红包界面跳转
				intent.setClass(RewardManagerActivity.this,RewardWithRedEnvelopeActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_reward_integration:
				//TODO  积分奖励界面跳转
				intent.setClass(RewardManagerActivity.this,RewardWithIntegralActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_reward_invitation:
				//TODO 邀请奖励界面跳转
				intent.setClass(RewardManagerActivity.this,RewardWithInviteActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_reward_coupon:
				//TODO 我的优惠券界面跳转
				intent.setClass(RewardManagerActivity.this,RewardWithCouponActivity.class);
				startActivity(intent);
				break;

		}
	}


	public void finish() {
		super.finish();
	}

}
