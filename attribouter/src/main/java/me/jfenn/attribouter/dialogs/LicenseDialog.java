package me.jfenn.attribouter.dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import android.view.View;
import android.widget.TextView;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.wedges.LicenseWedge;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class LicenseDialog extends AppCompatDialog {

    private LicenseWedge license;

    public LicenseDialog(Context context, LicenseWedge license) {
        super(context, ResourceUtils.getThemeResourceAttribute(context, R.styleable.AttribouterTheme_licenseDialogTheme, R.style.AttribouterTheme_Dialog));
        this.license = license;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_attribouter_license);

        TextView nameView = findViewById(R.id.name);
        TextView descriptionView = findViewById(R.id.description);
        View infoContainerView = findViewById(R.id.infoContainer);
        View permissionsView = findViewById(R.id.permissions);
        TextView permissionsTextView = findViewById(R.id.permissionsText);
        View conditionsView = findViewById(R.id.conditions);
        TextView conditionsTextView = findViewById(R.id.conditionsText);
        View limitationsView = findViewById(R.id.limitations);
        TextView limitationsTextView = findViewById(R.id.limitationsText);
        View bodyContainerView = findViewById(R.id.bodyContainer);
        TextView bodyView = findViewById(R.id.body);
        View moreInfoButton = findViewById(R.id.moreInfo);

        nameView.setText(ResourceUtils.getString(getContext(), license.licenseName));
        if (license.licenseDescription != null)
            descriptionView.setText(license.licenseDescription);
        else descriptionView.setVisibility(View.GONE);

        String permissions = license.getLicensePermissions();
        if (permissions != null)
            permissionsTextView.setText(permissions);
        else permissionsView.setVisibility(View.GONE);

        String conditions = license.getLicenseConditions();
        if (conditions != null)
            conditionsTextView.setText(conditions);
        else conditionsView.setVisibility(View.GONE);

        String limitations = license.getLicenseLimitations();
        if (limitations != null)
            limitationsTextView.setText(limitations);
        else limitationsView.setVisibility(View.GONE);

        if (permissions == null && conditions == null && limitations == null)
            infoContainerView.setVisibility(View.GONE);

        String body = ResourceUtils.getString(getContext(), license.licenseBody);
        if (body != null)
            bodyView.setText(body);
        else bodyContainerView.setVisibility(View.GONE);

        String moreInfo = ResourceUtils.getString(getContext(), license.licenseUrl);
        if (moreInfo != null)
            moreInfoButton.setOnClickListener(new UrlClickListener(moreInfo));
        else moreInfoButton.setVisibility(View.GONE);
    }
}
