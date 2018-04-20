package me.jfenn.attribouter.data.info.link;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import me.jfenn.attribouter.data.info.LicenseInfoData;
import me.jfenn.attribouter.dialogs.LicenseDialog;
import me.jfenn.attribouter.utils.UrlClickListener;

public class LicenseLinkInfoData extends LinkInfoData {

    private LicenseInfoData license;

    public LicenseLinkInfoData(@NonNull LicenseInfoData license, int priority) {
        super("license", "@string/title_attribouter_license", null, "@drawable/ic_attribouter_copyright", priority);
        this.license = license;
    }

    @Nullable
    @Override
    public View.OnClickListener getListener(Context context) {
        if (license.licenseBody != null) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LicenseDialog(v.getContext(), license)
                            .show();
                }
            };
        } else if (license.licenseUrl != null)
            return new UrlClickListener(license.licenseUrl);
        else return null;
    }
}
