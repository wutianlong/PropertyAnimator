package com.example.propertyanimatior;

import java.util.ArrayList;
import android.support.v7.app.ActionBarActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//参考文章  http://blog.csdn.net/alrdy/article/details/7836963
public class PropertyAnimatorTest extends ActionBarActivity implements
		OnClickListener {

	/**
	 * 1.ValueAnimator 主要用来显示属性当前值 2.ObjectAnimator主要用来播放动画
	 * 3.ObjectAnimator继承于ValueAnimator
	 * 
	 * 常用属性： translationX, translationY, rotation,
	 * rotationX,rotationY,scaleX,scaleY, X ,Y,alpha
	 * 
	 * 常用方法和类：1.动画：ValueAnimator,ObjectAnimator,2.属性值变化监听器：
	 * AnimatorUpdateListener
	 * ,AnimatorListenerAdapter,3.属性集合运转:PropertyValuesHolder
	 * ,AnimatorSet,4.时间插值规律TypeEvaluators,Interpolators
	 * 
	 * 时间插值器 AccelerateDecelerateInterpolator, AccelerateInterpolator,
	 * AnticipateInterpolator, AnticipateOvershootInterpolator,
	 * BounceInterpolator, CycleInterpolator, DecelerateInterpolator,
	 * LinearInterpolator, OvershootInterpolator, PathInterpolator
	 */
	private int[] imageIds = new int[] { R.id.redcircle, R.id.camera,
			R.id.music, R.id.location, R.id.moon, R.id.people, R.id.circle,
			R.id.message, };
	private ArrayList<ImageView> imageSrc = new ArrayList<ImageView>();

	private boolean mSwitchFlag = true;

	private TextView mTextValueAnimatorText;
	private ImageView mIcLauncherImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextValueAnimatorText = (TextView) findViewById(R.id.valute_animator_text);
		mIcLauncherImageView = (ImageView) findViewById(R.id.ic_launcher);
		mTextValueAnimatorText.setOnClickListener(this);
		mIcLauncherImageView.setOnClickListener(this);
		setImageView();
	}

	private void setImageView() {
		for (int i = 0; i < imageIds.length; i++) {
			ImageView imageView = (ImageView) findViewById(imageIds[i]);
			imageView.setOnClickListener(this);
			imageSrc.add(imageView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.redcircle:
			if (mSwitchFlag)
				startSingleOneAnimator();
			// startMutlBeforOrAfterAnimator();
			else
				closeAnimator();
			break;
		case R.id.ic_launcher:
			startMultiAnimator();
			break;
		case R.id.valute_animator_text:
			showValueAnimator();
			break;
		default:
			closeAnimator();
			break;
		}
	}

	// 只是跑一个动画
	@SuppressLint("NewApi")
	private void startSingleOneAnimator() {
		for (int i = 0; i < imageIds.length; i++) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(imageSrc.get(i),
					"translationY", 0, i * 203);
			animator.setDuration(600);
			animator.setInterpolator(new OvershootInterpolator());
			animator.setStartDelay(i * 300);
			animator.start();
		}
		mSwitchFlag = false;
	}

	// 同时跑多个动画，使用PropertyValueHolder 进行优化
	@SuppressLint("NewApi")
	private void startMultiAnimator() {
		PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("translationY",
				0, 300);
		PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("translationX",
				0, 300);
		PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("rotation", 0,
				360);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
				mIcLauncherImageView, p1, p2, p3);
		animator.setDuration(600);
		// 根据需要只是复写其中某个方法 onAnimationStart() ,onAnimationEnd()
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				Toast.makeText(PropertyAnimatorTest.this, "动画播放结束",
						Toast.LENGTH_SHORT).show();
			}
		});
		animator.start();
	}

	// 同时跑多个动画 ，分先后顺序
	@SuppressLint("NewApi")
	private void startMutlBeforOrAfterAnimator() {
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(imageSrc.get(0),
				"translationX", dp2px(200), 500);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(imageSrc.get(0),
				"translationY", 0, 300);
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageSrc.get(0),
				"rotation", 0, 360);

		AnimatorSet animatorSet = new AnimatorSet();
		// 依次顺序跑
		// animatorSet.playSequentially(animator1, animator2, animator3);

		// 同时跑animator1,animator2 之后再跑animator3
		// animatorSet.play(animator1).with(animator2).before(animator3);

		// 同时跑动画
		animatorSet.playTogether(animator1, animator2, animator3);

		animatorSet.setDuration(600);
		animatorSet.start();
	}

	@SuppressLint("NewApi")
	private void closeAnimator() {
		for (int i = 1; i < imageIds.length; i++) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(imageSrc.get(i),
					"translationY", i * 190, 0);
			animator.setDuration(600);
			animator.setInterpolator(new BounceInterpolator());
			animator.setStartDelay(i * 300);
			animator.start();
		}
		mSwitchFlag = true;
	}

	// ValueAnimator获取每一时刻属性值
	@SuppressLint("NewApi")
	private void showValueAnimator() {
		ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Integer value = (Integer) animation.getAnimatedValue();
				mTextValueAnimatorText.setText(value + "");
			}
		});

		valueAnimator.setDuration(2000);
		valueAnimator.start();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				this.getResources().getDisplayMetrics());
	}
}
