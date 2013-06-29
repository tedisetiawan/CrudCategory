package dlmbg.pckg.crud.category;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;

/*
 * Gede Lumbung - 2013
 * http://gedelumbung.com
 * Just Simple Android CRUD App with Parent Child Content
 */

public class DetailPenemu extends Activity{

	private TextView nama_penemu_et, kelahiran_et, keterangan_et;
	private ImageView gambar_iv;
	
	String nama_penemu,kelahiran,keterangan,gambar;
	Button btnShare;
	
	private Facebook mFacebook;
	private CheckBox mFacebookCb;
	private ProgressDialog mProgress;
	
	private Handler mRunOnUi = new Handler();
	
	private static final String APP_ID = "176485612512806";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.detail);

		nama_penemu_et = (TextView) findViewById(R.id.nama_penemu_detail);
		kelahiran_et = (TextView) findViewById(R.id.kelahiran_detail);
		keterangan_et = (TextView) findViewById(R.id.keterangan_detail);
		gambar_iv = (ImageView) findViewById(R.id.gambar_detail);
		
		Bundle extras = getIntent().getExtras();
		nama_penemu = extras.getString("nama_penemu");
		kelahiran = extras.getString("kelahiran");
		keterangan = extras.getString("keterangan");
		gambar = extras.getString("gambar");

		nama_penemu_et.setText(nama_penemu);
		kelahiran_et.setText(kelahiran);
		keterangan_et.setText(keterangan);
		
		Bitmap bmImg = BitmapFactory.decodeFile(gambar);
		gambar_iv.setImageBitmap(bmImg);
		
		String imageInSD = gambar;      
		Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
		gambar_iv.setImageBitmap(bitmap);
		
		mFacebookCb				  = (CheckBox) findViewById(R.id.cb_facebook);
		
		mProgress	= new ProgressDialog(this);
		
		mFacebook 	= new Facebook(APP_ID);
		
		SessionStore.restore(mFacebook, this);

		if (mFacebook.isSessionValid()) {
			mFacebookCb.setChecked(true);
				
			String name = SessionStore.getName(this);
			name		= (name.equals("")) ? "Unknown" : name;
				
			mFacebookCb.setText("  Facebook  (" + name + ")");
		}
		
		 btnShare = (Button) findViewById(R.id.btn_share);
			btnShare.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String review = nama_penemu+"\n\n"+keterangan;
					
					if (review.equals("")) return;
				
					if (mFacebookCb.isChecked()) postToFacebook(review);
				}
			});
		

	}
	
	private void postToFacebook(String review) {	
		mProgress.setMessage("Posting ...");
		mProgress.show();
		
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		
		Bundle params = new Bundle();
    		
		params.putString("message", review);
		
		mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
	}

	private final class WallPostListener extends BaseRequestListener {
        public void onComplete(final String response) {
        	mRunOnUi.post(new Runnable() {
        		@Override
        		public void run() {
        			mProgress.cancel();
        			
        			Toast.makeText(DetailPenemu.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
        		}
        	});
        }
    }

}
