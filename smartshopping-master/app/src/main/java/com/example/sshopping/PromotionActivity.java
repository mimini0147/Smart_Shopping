package com.example.sshopping;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.sshopping.http.OnDataReturnListener;
import com.example.sshopping.views.ISlideMenuActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import SmartShopping.OV.OVCommande;
import SmartShopping.OV.OVPromotion;
import SmartShopping.OV.OVUtilisateur;
import SmartShopping.OV.RepPromotion;
import SmartShopping.OV.ReqUtilisateur;
import SmartShopping.Remote.WebServer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PromotionActivity  extends Activity implements ISlideMenuActivity{

	private DrawerLayout	_drawerLayout;
	private List<OVPromotion> _promotionList = null;
	private TableLayout _tablePromotion;

	/***
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_promotion);
		this._drawerLayout = (DrawerLayout)this.findViewById(R.id.main_container);
		this._tablePromotion = (TableLayout)this.findViewById(R.id.table_primotion);

		List<NameValuePair> nvp = new ArrayList<NameValuePair>();

		ReqUtilisateur reqUtilisateur = new ReqUtilisateur();
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String deviceID = mngr.getDeviceId();
		deviceID = deviceID.substring(deviceID.length()-8, deviceID.length());

		OVUtilisateur ovUtilisateur = new OVUtilisateur(Integer.parseInt(deviceID));
		try {
			nvp.add(new BasicNameValuePair("Utilisateur", ovUtilisateur.toJSON().toString()));
		}catch (Exception e){
			Log.i("Promotion LOG", e.getMessage());
		}
		WebServer.getInstance().sendRequest(WebServer.COMMANDE.GetPromotions, nvp, new OnDataReturnListener(){

			@Override
			public void OnDataReturn(JSONObject jobj) {
				Log.i("-------", jobj.toString());
				RepPromotion repP = new RepPromotion(jobj);
				PromotionActivity.this._promotionList = repP.getListePromotion();
				PromotionActivity.this.ShowPromotions();
			}

		});

	}

	/**
	 * Afficher la liste des promotions
	 */
	public void ShowPromotions(){
		for(OVPromotion promo : this._promotionList){
			final TableRow tableRow = new TableRow(this);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
			TextView tv = new TextView(this);
			tv.setText(promo.toString());

			final Button qrButton  = new Button(this);
			qrButton.setText("QR");
			qrButton.setTag(promo);
			qrButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					AlertDialog.Builder builder = new AlertDialog.Builder(PromotionActivity.this);

					// 2. Chain together various setter methods to set the dialog characteristics
					builder.setTitle("QR code")
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

					QRCodeWriter qrCode = new QRCodeWriter();
					BitMatrix bm;
					try {
						bm = qrCode.encode(qrButton.getTag().toString(), BarcodeFormat.QR_CODE, 450, 450);
						Bitmap mBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
						for (int i = 0; i < bm.getWidth(); i++) {
							for (int j = 0; j < bm.getHeight(); j++) {
								mBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
							}
						}

						ImageView img = new ImageView(PromotionActivity.this);

						if (bm != null) {
							img.setImageBitmap(mBitmap);
						}
						builder.setView(img);
						// 3. Get the AlertDialog from create()
						AlertDialog dialog = builder.create();

						dialog.show();
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}


			});
			tableRow.addView(tv);
			tableRow.addView(qrButton);
			this._tablePromotion.addView(tableRow);
		}
	}

	@Override
	public void closeSlideMenu() {
		// TODO Auto-generated method stub
		this._drawerLayout.closeDrawer(Gravity.LEFT);
	}

	@Override
	public void openSlideMenu() {
		this._drawerLayout.openDrawer(Gravity.LEFT);
	}
}
