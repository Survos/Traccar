
package com.survos.tracker.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.survos.tracker.Constants.Constants;
import com.survos.tracker.R;
import com.survos.tracker.data.Logger;

/**
 * Alert Dialog to display a custom view to agreement of the app
 *
 * @author Anshul Kamboj
 */
public class AgreementDialog extends DialogFragment {

    private static final String TAG = "BroadcastMessageDialogFragment";

    /** Res Id for the Dialog Title. */
    private int                 mTitleId;

    /** Res Id for the Positive button label. */
    private int                 mPositiveLabelId;

    /** Res Id for the negative Button label. */
    private int                 mNegativeLabelId;

    /** Res Id for the neutral button label. */
    private int                 mNeutralLabelId;

    /** Click Listener for the Dialog buttons. */
    private OnClickListener     mClickListener;

    /** On Dismiss Listener for the Dialog */
    private OnDismissListener   mOnDismissListener;

    /** Resource Id for the icon to be be used in the alert dialog. */
    private int                 mIconId;

    /** Boolean flag to identify if the dialog is cancelable. */
    private boolean             isCancellable;

    /** Res Id for the hint label. */
    private int                 mHintLabelId;

    /** Resource Id for the theme to be used for the alert dialog. */
    private int                 mTheme;

//    private TextView            mAgreementText;

    private String              mAgreementString;

    ProgressDialog pd;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mTitleId = savedInstanceState.getInt(DialogKeys.TITLE_ID);
            mNegativeLabelId = savedInstanceState
                            .getInt(DialogKeys.NEGATIVE_LABEL_ID);
            mNeutralLabelId = savedInstanceState
                            .getInt(DialogKeys.NEUTRAL_LABEL_ID);
            mPositiveLabelId = savedInstanceState
                            .getInt(DialogKeys.POSITIVE_LABEL_ID);
            isCancellable = savedInstanceState
                            .getBoolean(DialogKeys.CANCELLABLE);
            mIconId = savedInstanceState.getInt(DialogKeys.ICON_ID);
            mTheme = savedInstanceState.getInt(DialogKeys.THEME);
        }

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        final Builder builder = new Builder(getActivity(), mTheme);

        final View contentView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_agreement_dialog, null);

//        mAgreementText = (TextView) contentView.findViewById(R.id.agreement_text);

        WebView wv_ReadMe = (WebView) contentView.findViewById(R.id.ReadMe_WV);
//        wv_ReadMe.loadUrl("file:///android_asset/agreement.html");
        WebSettings webSettings = wv_ReadMe.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);


//        mAgreementText.setText(mAgreementString);

        wv_ReadMe.getSettings()
                .setUserAgentString(
                        "Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");

        wv_ReadMe.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }
        });

        wv_ReadMe.loadUrl(Constants.AGREEMENT_URL);


        builder.setView(contentView);

        if (mIconId != 0) {
            builder.setIcon(mIconId);
        }
        if (mTitleId != 0) {
            builder.setTitle(mTitleId);
        }

        if (mPositiveLabelId != 0) {
            builder.setPositiveButton(mPositiveLabelId, mClickListener);
        }

        if (mNegativeLabelId != 0) {
            builder.setNegativeButton(mNegativeLabelId, mClickListener);
        }

        if (mNeutralLabelId != 0) {
            builder.setNeutralButton(mNeutralLabelId, mClickListener);
        }

        builder.setCancelable(isCancellable);
        setCancelable(isCancellable);
        return builder.create();
    }




    @Override
    public void onAttach(final Activity activity) {

        super.onAttach(activity);

        if (activity instanceof OnClickListener) {
            mClickListener = (OnClickListener) activity;
        }

        else {
            throw new IllegalStateException("Activity must implement DialogInterface.OnClickListener");
        }

        if (activity instanceof OnDismissListener) {
            mOnDismissListener = (OnDismissListener) activity;
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {

        outState.putInt(DialogKeys.TITLE_ID, mTitleId);
        outState.putInt(DialogKeys.NEGATIVE_LABEL_ID, mNegativeLabelId);
        outState.putInt(DialogKeys.POSITIVE_LABEL_ID, mPositiveLabelId);
        outState.putInt(DialogKeys.NEUTRAL_LABEL_ID, mNeutralLabelId);
        outState.putBoolean(DialogKeys.CANCELLABLE, isCancellable);
        outState.putInt(DialogKeys.ICON_ID, mIconId);
        outState.putInt(DialogKeys.THEME, mTheme);
        outState.putInt(DialogKeys.HINT_LABEL_ID, mHintLabelId);
        super.onSaveInstanceState(outState);
    }

    public void show(final int theme, final int iconId, final int titleId,
                    final int positiveLabelId, final int negativeLabelId,
                    final int neutralLabelId,final int hintLabelId, final FragmentManager manager,
                    final boolean cancellable, final String fragmentTag,
                    final String agreementText) {

        mTheme = theme;
        mIconId = iconId;
        mTitleId = titleId;
        mPositiveLabelId = positiveLabelId;
        mNegativeLabelId = negativeLabelId;
        mNeutralLabelId = neutralLabelId;
        isCancellable = cancellable;
        mHintLabelId=hintLabelId;
        mAgreementString = agreementText;

        try {
            super.show(manager, fragmentTag);
        } catch (final IllegalStateException e) {
            Logger.e(TAG, e, "Exception");
        }

    }



}
