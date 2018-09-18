package me.jfenn.attribouter.wedges.link;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import me.jfenn.attribouter.wedges.LicenseWedge;
import me.jfenn.attribouter.dialogs.LicenseDialog;
import me.jfenn.attribouter.utils.UrlClickListener;

public class LicenseLinkWedge extends LinkWedge {

    private LicenseWedge license;

    public LicenseLinkWedge(@NonNull LicenseWedge license, int priority) {
        super("license", "@string/title_attribouter_license", null, "@drawable/ic_attribouter_copyright", false, priority);
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
