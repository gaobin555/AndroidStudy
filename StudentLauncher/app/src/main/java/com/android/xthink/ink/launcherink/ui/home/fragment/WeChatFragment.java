package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IWeChatPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.Presenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IWeChatView;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.UiTestUtils;
import com.android.xthink.ink.launcherink.utils.FreezeUtils;


/**
 * 微信页面
 * Created by wanchi on 2017/3/6.
 */

public class WeChatFragment extends NativeFragment implements IWeChatView, View.OnClickListener {

    private static final String TAG = "JvWechatFragment";
    private static final String TAG_LIFE = "FragmentLife";

    private IWeChatPresenter mWeChatPresenter;
    private ViewGroup mMsgContainer;
    private LayoutInflater mInflater;
    private View mWeChatFriendBtn;
    private View mWeChatSubscribeBtn;
    private View mWeChatCollectionBtn;
    private View mWeChatLauncherBtn;

    private View mSimpleMsgContainer;
    private TextView mSimpleMsgTv;

    public static WeChatFragment newInstance() {
        Bundle args = new Bundle();
        WeChatFragment fragment = new WeChatFragment();
        fragment.setArguments(args);
        MyLog.d(TAG_LIFE, "newInstance" + "WeChatFragment");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(TAG_LIFE, "onCreate" + "WeChatFragment");
    }

    @Override
    public void onAttach(Activity activity) {
        Presenter.bind(this, IWeChatPresenter.class);
        super.onAttach(activity);
    }

    @Override
    public void addPresenter(IPresenter presenter) {
        super.addPresenter(presenter);
        if (presenter instanceof IWeChatPresenter) {
            mWeChatPresenter = (IWeChatPresenter) presenter;
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View rootView = inflater.inflate(R.layout.fragment_wechat, container, false);
        mMsgContainer = (ViewGroup) rootView.findViewById(R.id.fragment_wechat_container);

        mSimpleMsgContainer = rootView.findViewById(R.id.fragment_wechat_simple_msg_container);
        mSimpleMsgTv = (TextView) rootView.findViewById(R.id.fragment_wechat_simple_msg_tv);

        mWeChatFriendBtn = rootView.findViewById(R.id.fragment_wechat_friend);
        mWeChatSubscribeBtn = rootView.findViewById(R.id.fragment_wechat_subscribe);
        mWeChatCollectionBtn = rootView.findViewById(R.id.fragment_wechat_collection);
        mWeChatLauncherBtn = rootView.findViewById(R.id.fragment_wechat_launcher);

        return rootView;
    }

    @Override
    protected void initData() {

        //do nothing
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        if (mWeChatPresenter != null) {
            mWeChatPresenter.rebindIfNecessary(getActivity());
            mWeChatPresenter.loadWechatMessage();
            mWeChatPresenter.loadWechatActivityNameIfNeeded();
        }
    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
        if (isVisibleToUser && mWeChatPresenter != null) {
            mWeChatPresenter.rebindIfNecessary(getActivity());
            mWeChatPresenter.loadWechatActivityNameIfNeeded();
        }
    }

    @Override
    protected void setListener() {
        //do nothing
        mWeChatCollectionBtn.setOnClickListener(this);
        mWeChatFriendBtn.setOnClickListener(this);
        mWeChatSubscribeBtn.setOnClickListener(this);
        mWeChatLauncherBtn.setOnClickListener(this);
        mSimpleMsgContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // 点击按钮，wechat冻结
        if (FreezeUtils.isAppFrozen(mContext, InkConstants.PACKAGE_NAME_WECHAT)) {
            FreezeUtils.handleAppFreeze(mContext, InkConstants.PACKAGE_NAME_WECHAT);
            return;
        }

        int id = v.getId();
        switch (id) {
            case R.id.fragment_wechat_friend:
                // 朋友圈
                UiTestUtils.tag("点击了朋友圈");
                mWeChatPresenter.openWeChatFriend();
                break;
            case R.id.fragment_wechat_subscribe:
                // 订阅号
                UiTestUtils.tag("点击了订阅号");
                mWeChatPresenter.openWeChatSubScribe();
                break;
            case R.id.fragment_wechat_collection:
                // 收藏
                UiTestUtils.tag("点击了收藏");
                mWeChatPresenter.openWeChatFavorite();
                break;
            case R.id.fragment_wechat_launcher:
                UiTestUtils.tag("点击了wechat");
                mWeChatPresenter.openWechatLauncher();

                break;
            case R.id.fragment_wechat_simple_msg_container:
                UiTestUtils.tag("点击了wechat消息");
                String title = (String) v.getTag();
                if (!TextUtils.isEmpty(title)) {
                    mWeChatPresenter.openNotification(title);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceiveNotification(String title, String content, Drawable drawable, String time, boolean isSimpleMsg) {
        View notificationItemView = createNotificationItem(mMsgContainer, title, content, drawable, time);

        if (isSimpleMsg) {
            mMsgContainer.removeAllViews();
            mSimpleMsgContainer.setTag(title);
            showSimpleMsg(content);
        } else {
            mMsgContainer.addView(notificationItemView);
            mSimpleMsgContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void removeAllNotification() {
        mMsgContainer.removeAllViews();
        mSimpleMsgContainer.setVisibility(View.GONE);
    }

    private void showSimpleMsg(String msg) {
        mSimpleMsgContainer.setVisibility(View.VISIBLE);
        mSimpleMsgTv.setText(msg);

    }

    private View createNotificationItem(ViewGroup viewGroup, String title, String content, Drawable drawable, String time) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getActivity());
        }
        View notificationItemView = mInflater.inflate(R.layout.item_notification, viewGroup, false);

        // 设置头像
        ImageView iv = (ImageView) notificationItemView.findViewById(R.id.item_notification_iv);
        if (drawable != null) {
            iv.setImageDrawable(drawable);
        }

        // 设置标题
        TextView titleTv = (TextView) notificationItemView.findViewById(R.id.item_notification_title_tv);
        titleTv.setText(title);

        // 设置内容
        TextView contentTv = (TextView) notificationItemView.findViewById(R.id.item_notification_content_tv);
        contentTv.setText(content);

        // 时间
        TextView timeTv = (TextView) notificationItemView.findViewById(R.id.item_notification_time_tv);
        timeTv.setText(time);

        notificationItemView.setTag(title);
        notificationItemView.setOnClickListener(mOnNotificationClickListener);

        return notificationItemView;
    }

    private View.OnClickListener mOnNotificationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = (String) v.getTag();
            if (title == null) {
                return;
            }
            mWeChatPresenter.openNotification(title);
        }
    };

    @Override
    public void showOpenFailed(boolean isLaunch) {
        if (!isLaunch) {
            mWeChatPresenter.openWechatLauncher();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.d(TAG_LIFE, "onDestroy" + "WeChatFragment");
    }

    public static final String mPageName = "WeChatFragment";

    /**
     * 用于App统计
     */
    public void onFragmentResume(Context context) {
        MyLog.d(TAG, "cy--=fragment onResume=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
    }

    public void onFragmentPause(Context context) {
        MyLog.d(TAG, "cy--=fragment onPause=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
    }
}
