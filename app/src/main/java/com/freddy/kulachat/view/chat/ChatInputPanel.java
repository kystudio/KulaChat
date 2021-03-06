package com.freddy.kulachat.view.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.freddy.kulachat.R;
import com.freddy.kulachat.config.AppConfig;
import com.freddy.kulachat.utils.DensityUtil;
import com.freddy.kulachat.utils.UIUtil;
import com.freddy.kulachat.widget.CImageButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

/**
 * @author FreddyChen
 * @name
 * @date 2020/06/02 20:15
 * @email chenshichao@outlook.com
 * @github https://github.com/FreddyChen
 * @desc
 */
public class ChatInputPanel extends LinearLayout {

    private Unbinder unbinder;
    private Context mContext;
    @BindView(R.id.btn_voice)
    CImageButton mVoiceBtn;
    @BindView(R.id.btn_expression)
    CImageButton mExpressionBtn;
    @BindView(R.id.btn_more)
    CImageButton mMoreBtn;
    @BindView(R.id.et_content)
    ChatEditText mContentEditText;
    private PanelType panelType = PanelType.NONE;
    private PanelType lastPanelType = panelType;
    private boolean isKeyboardOpened;
    private int keyboardHeight;

    private boolean isActive = false;

    public enum PanelType {
        INPUT_MOTHOD,
        EXPRESSION,
        MORE,
        NONE
    }

    public ChatInputPanel(Context context) {
        this(context, null);
    }

    public ChatInputPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatInputPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_chat_input_panel, this, true);
        unbinder = ButterKnife.bind(this, view);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        keyboardHeight = AppConfig.readKeyboardHeight();
        setOrientation(HORIZONTAL);
        setPadding(DensityUtil.dp2px(10), DensityUtil.dp2px(6), DensityUtil.dp2px(10), DensityUtil.dp2px(6));
        setGravity(Gravity.BOTTOM);
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.c_77cbcbcb));
        mContentEditText.setInputType(InputType.TYPE_NULL);
        mContentEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!isKeyboardOpened) {
                    UIUtil.requestFocus(mContentEditText);
                    UIUtil.showSoftInput(mContext, mContentEditText);
                    mContentEditText.resetInputType();
                    mExpressionBtn.setNormalImageResId(R.drawable.ic_chat_expression_normal);
                    mExpressionBtn.setPressedImageResId(R.drawable.ic_chat_expression_pressed);
                    handleAnimator(PanelType.INPUT_MOTHOD);
                    if (mOnChatPanelStateListener != null) {
                        mOnChatPanelStateListener.onShowInputMethodPanel();
                    }
                }
                return true;
            }

            return false;
        });
    }

    @OnClick({R.id.btn_voice, R.id.btn_expression, R.id.btn_more})
    void onClickListeners(View v) {
        switch (v.getId()) {
            case R.id.btn_voice: {
                break;
            }
            case R.id.btn_expression: {
                if (lastPanelType == PanelType.EXPRESSION) {
                    mExpressionBtn.setNormalImageResId(R.drawable.ic_chat_expression_normal);
                    mExpressionBtn.setPressedImageResId(R.drawable.ic_chat_expression_pressed);
                    UIUtil.requestFocus(mContentEditText);
                    UIUtil.showSoftInput(mContext, mContentEditText);
                    handleAnimator(PanelType.INPUT_MOTHOD);
                    mContentEditText.resetInputType();
                } else {
                    mExpressionBtn.setNormalImageResId(R.drawable.ic_chat_keyboard_normal);
                    mExpressionBtn.setPressedImageResId(R.drawable.ic_chat_keyboard_pressed);
                    UIUtil.loseFocus(mContentEditText);
                    UIUtil.hideSoftInput(mContext, mContentEditText);
                    handleAnimator(PanelType.EXPRESSION);
                    if (mOnChatPanelStateListener != null) {
                        mOnChatPanelStateListener.onShowExpressionPanel();
                    }
                }
                break;
            }
            case R.id.btn_more: {
                break;
            }
        }
    }

    private void handleAnimator(PanelType panelType) {
        Log.d("ChatInputPanel", "lastPanelType = " + lastPanelType + "\tpanelType = " + panelType);
        if(lastPanelType == panelType) {
            return;
        }
        isActive = true;
        Log.d("ChatInputPanel", "isActive = " + isActive);
        this.panelType = panelType;
        float fromValue = 0.0f, toValue = 0.0f;
        switch (panelType) {
            case INPUT_MOTHOD:
                switch (lastPanelType) {
                    case EXPRESSION:
                        fromValue = -keyboardHeight - DensityUtil.dp2px(36);
                        toValue = -keyboardHeight;
                        break;
                    case MORE:
                        break;
                    case NONE:
                        fromValue = 0.0f;
                        toValue = -keyboardHeight;
                        break;
                }
                break;
            case EXPRESSION:
                switch (lastPanelType) {
                    case INPUT_MOTHOD:
                        fromValue = -keyboardHeight;
                        toValue = -keyboardHeight - DensityUtil.dp2px(36);
                        break;
                    case MORE:
                        break;
                    case NONE:
                        fromValue = 0.0f;
                        toValue = -keyboardHeight - DensityUtil.dp2px(36);
                        break;
                }
                break;
            case MORE:
                switch (lastPanelType) {
                    case INPUT_MOTHOD:
                        break;
                    case EXPRESSION:
                        break;
                    case NONE:
                        break;
                }
                break;
            case NONE:
                switch (lastPanelType) {
                    case INPUT_MOTHOD:
                        fromValue = -keyboardHeight;
                        toValue = 0.0f;
                        break;
                    case EXPRESSION:
                        fromValue = -keyboardHeight - DensityUtil.dp2px(36);
                        toValue = 0.0f;
                        break;
                    case MORE:
                        break;
                }
                break;
        }

        if (mOnLayoutAnimatorHandleListener != null) {
            mOnLayoutAnimatorHandleListener.handleAnimator(fromValue, toValue);
        }

        lastPanelType = panelType;
    }

    @OnEditorAction(R.id.et_content)
    boolean onContentEditTextEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            Toasty.success(mContext, "发送", Toasty.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private OnLayoutAnimatorHandleListener mOnLayoutAnimatorHandleListener;

    public void setOnLayoutAnimatorHandleListener(OnLayoutAnimatorHandleListener listener) {
        this.mOnLayoutAnimatorHandleListener = listener;
    }

    public interface OnLayoutAnimatorHandleListener {
        void handleAnimator(float fromValue, float toValue);
    }

    public void reset() {
        if(!isActive) {
            return;
        }
        Log.d("ChatInputPanel", "reset()");
        UIUtil.loseFocus(mContentEditText);
        UIUtil.hideSoftInput(mContext, mContentEditText);
        mExpressionBtn.setNormalImageResId(R.drawable.ic_chat_expression_normal);
        mExpressionBtn.setPressedImageResId(R.drawable.ic_chat_expression_pressed);
        handleAnimator(PanelType.NONE);
        isActive = false;
        Log.d("ChatInputPanel", "isActive = " + isActive);
    }

    public void release() {
        Log.d("ChatInputPanel", "release()");
        reset();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }

    public void onSoftKeyboardOpened() {
        isKeyboardOpened = true;
        mContentEditText.resetInputType();
    }

    public void onSoftKeyboardClosed() {
        isKeyboardOpened = false;
        mContentEditText.setInputType(InputType.TYPE_NULL);
        if(lastPanelType == PanelType.INPUT_MOTHOD) {
            UIUtil.loseFocus(mContentEditText);
            UIUtil.hideSoftInput(mContext, mContentEditText);
            handleAnimator(PanelType.NONE);
        }
    }

    public void setKeyboardHeight(int keyboardHeight) {
        this.keyboardHeight = keyboardHeight;
    }

    private OnChatPanelStateListener mOnChatPanelStateListener;

    public void setOnChatPanelStateListener(OnChatPanelStateListener listener) {
        this.mOnChatPanelStateListener = listener;
    }

    public interface OnChatPanelStateListener {
        void onShowInputMethodPanel();
        void onShowExpressionPanel();
    }
}
