package com.tieyouin.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;
import com.tieyouin.R;
import com.tieyouin.Service.GpsInfo;
import com.tieyouin.application.iMateApplication;
import com.tieyouin.custom.CustomActivity;
import com.tieyouin.model.Data;
import com.tieyouin.ui.Chat;
import com.tieyouin.ui.FindMatch;
import com.tieyouin.ui.LeftNavAdapter;
import com.tieyouin.ui.MainFragment;
import com.tieyouin.ui.Match;
import com.tieyouin.ui.Profile;
import com.tieyouin.ui.RightNavAdapter;
import com.tieyouin.ui.Settings;
import com.tieyouin.utils.BitmapUtils;
import com.tieyouin.utils.Common;
import com.tieyouin.utils.Constant;
import com.tieyouin.utils.MultiPartRequest;
import com.tieyouin.utils.ReqConst;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The Class MainActivity is the base activity class of the application. This
 * activity is launched after the Login and it holds all the Fragments used in
 * the app. It also creates the Navigation Drawers on left and right side.
 */

@SuppressWarnings("deprecation")
public class MainActivity extends CustomActivity
{

	/** The drawer layout. */
	private DrawerLayout drawerLayout;

	/** ListView for left side drawer. */
	private ListView drawerLeft;

	/** ListView for right side drawer. */
	private ListView drawerRight;

	/** The drawer toggle. */
	private ActionBarDrawerToggle drawerToggle;

	/** The left navigation list adapter. */
	private LeftNavAdapter adapter;

	private String strUserName;
	private TextView ui_text_username;
	private CircleImageView ui_imv_profile;

    private ImageLoader _imageLoader;
    private Uri _imageCaptureUri;
    private String _photoPath = "";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_GALLERY = 1;
    private static final int CROP_FROM_CAMERA = 2;

	/* (non-Javadoc)
	 * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		strUserName = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_NAME, "");

		setupDrawer();
		setupContainer();

        // down profile image
        _imageLoader = iMateApplication.getInstance().getImageLoader();

		downloadProfile();
    }

    @Override
    protected void onDestroy() {

        if(Common.isGpsServiceRunning(this, "com.imate.Service.GpsInfo")) {
            Intent serviceIntent = new Intent(this, GpsInfo.class);
            stopService(serviceIntent);
        }

        super.onDestroy();
    }

    /**
	 * Setup the drawer layout. This method also includes the method calls for
	 * setting up the Left & Right side drawers.
	 */
	private void setupDrawer()
	{
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View view)
			{
				setActionBarTitle();
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				setActionBarTitle();
				invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		drawerLayout.closeDrawers();

		setupLeftNavDrawer();
		setupRightNavDrawer();
	}

	/**
	 * Setup the left navigation drawer/slider. You can add your logic to load
	 * the contents to be displayed on the left side drawer. It will also setup
	 * the Header and Footer contents of left drawer. This method also apply the
	 * Theme for components of Left drawer.
	 */
	private void setupLeftNavDrawer()
	{
		drawerLeft = (ListView) findViewById(R.id.left_drawer);

		View header = getLayoutInflater().inflate(R.layout.left_nav_header, null);

		ui_text_username = (TextView) header.findViewById(R.id.user_name);
		ui_text_username.setText(strUserName);

		ui_imv_profile = (CircleImageView) header.findViewById(R.id.user_image);

		drawerLeft.addHeaderView(header);

		adapter = new LeftNavAdapter(this, getResources().getStringArray(
				R.array.arr_left_nav_list)); // home, my chat, location, setting, invite friend
		drawerLeft.setAdapter(adapter);
		drawerLeft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				drawerLayout.closeDrawers();
				if (pos != 0)
					launchFragment(pos - 1);
				else
					showImagePicker();

//                    launchFragment(-2);

			}
		});

	}

	/**
	 * Setup the right navigation drawer/slider. You can add your logic to load
	 * the contents to be displayed on the right side drawer. It will also setup
	 * the Header contents of right drawer.
	 */
	private void setupRightNavDrawer()
	{
		drawerRight = (ListView) findViewById(R.id.right_drawer);

		View header = getLayoutInflater().inflate(R.layout.rigth_nav_header,
				null);
		header.setClickable(true);
		drawerRight.addHeaderView(header);

		ArrayList<Data> al = new ArrayList<Data>();
		al.add(new Data("Emely", R.drawable.img_f1));
		al.add(new Data("John", R.drawable.img_f2));
		al.add(new Data("Aaliyah", R.drawable.img_f3));
		al.add(new Data("Valentina", R.drawable.img_f4));
		al.add(new Data("Barbara", R.drawable.img_f5));

		ArrayList<Data> al1 = new ArrayList<Data>(al);
		al1.addAll(al);
		al1.addAll(al);
		al1.addAll(al);

		drawerRight.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				drawerLayout.closeDrawers();
				launchFragment(1);
			}
		});
		drawerRight.setAdapter(new RightNavAdapter(this, al1));
	}

	/**
	 * Setup the container fragment for drawer layout. This method will setup
	 * the grid view display of main contents. You can customize this method as
	 * per your need to display specific content.
	 */
	private void setupContainer()
	{
		getSupportFragmentManager().addOnBackStackChangedListener(
				new OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						setActionBarTitle();
					}
				});
		launchFragment(0);
	}

	/**
	 * This method can be used to attach Fragment on activity view for a
	 * particular tab position. You can customize this method as per your need.
	 * 
	 * @param pos
	 *            the position of tab selected.
	 */
	public void launchFragment(int pos)
	{
		Fragment f = null;
		String title = null;
		if (pos == -1)          // -1
		{
			title = "Your Match";
			f = new Match();
		}
		else if (pos == -2)     // header view click
		{
			title = "Profile";
			f = new Profile();
		}
		else if (pos == 0)
		{
			title = "Home";
			f = new MainFragment();
		}
		else if (pos == 1)
		{
			title = "Chat with Nikita";
			f = new Chat();
		}
		else if (pos == 2)
		{
			title = "Find Match";
			f = new FindMatch();
		}
		else if (pos == 3)
		{
			title = "Settings";
			f = new Settings();
		}else if (pos == 4){

		}else if(pos == 5){
			gotoLogin();
		}

		if (f != null)
		{
//			while (getSupportFragmentManager().getBackStackEntryCount() > 0)
//			{
//				getSupportFragmentManager().popBackStackImmediate();
//			}
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, f).addToBackStack(title)
					.commit();
			if (adapter != null && pos >= 0)
				adapter.setSelection(pos);
		}
	}

	/**
	 * Set the action bar title text.
	 */
	private void setActionBarTitle()
	{
		if (drawerLayout.isDrawerOpen(drawerLeft))
		{
			getActionBar().setTitle("Main Menu");
			return;
		}
		if (drawerLayout.isDrawerOpen(drawerRight))
		{
			getActionBar().setTitle(R.string.all_matches);
			return;
		}

		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
			return;
		String title = getSupportFragmentManager().getBackStackEntryAt(
				getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName();
		getActionBar().setTitle(title);
		// getActionBar().setLogo(R.drawable.icon);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/* (non-Javadoc)
	 * @see com.newsfeeder.custom.CustomActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		if (drawerLayout.isDrawerOpen(drawerLeft)
				|| drawerLayout.isDrawerOpen(drawerRight)){
			menu.findItem(R.id.menu_chat).setVisible(false);
			menu.findItem(R.id.menu_alarm).setVisible(false);
			menu.findItem(R.id.menu_request).setVisible(false);
		}

		ActionItemBadge.update(this, menu.findItem(R.id.menu_chat), getResources().getDrawable(R.drawable.ic_chat), ActionItemBadge.BadgeStyles.RED, 13);
		ActionItemBadge.update(this, menu.findItem(R.id.menu_request), getResources().getDrawable(R.drawable.ic_request), ActionItemBadge.BadgeStyles.RED, 5);
		ActionItemBadge.update(this, menu.findItem(R.id.menu_alarm), getResources().getDrawable(R.drawable.ic_alarm), ActionItemBadge.BadgeStyles.RED, 8);

		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content
		// view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see com.newsfeeder.custom.CustomActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home
				&& drawerLayout.isDrawerOpen(drawerRight))
		{
			drawerLayout.closeDrawer(drawerRight);
			return true;
		}
		if (drawerToggle.onOptionsItemSelected(item))
		{
			drawerLayout.closeDrawer(drawerRight);
			return true;
		}

		if (item.getItemId() == R.id.menu_chat)
		{
			drawerLayout.closeDrawer(drawerLeft);
			if (!drawerLayout.isDrawerOpen(drawerRight))
				drawerLayout.openDrawer(drawerRight);
			else
				drawerLayout.closeDrawer(drawerRight);
		}
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (getSupportFragmentManager().getBackStackEntryCount() > 1)
			{
				getSupportFragmentManager().popBackStackImmediate();
			}
			else {
//                finish();
//                gotoLogin();
            }
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    /*
     * download profile image from server
     */
    private void downloadProfile() {

        String url = "http://192.168.0.40/mytest/index.php/api/getUserInfo";

		Log.d("url", "----------------"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseUserProfileDownload(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Error", "----------------" + volleyError.toString());
                Log.d("download", "--------------------------"+"Fail to download profile");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                JSONObject value = new JSONObject();
                try{
                    value.put("email", "mighty3star27@gmail.com");//Common.userName
                    params.put("request_info", value.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return params;
            }
        };

		iMateApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    private void parseUserProfileDownload(String json) {

        try {

            JSONObject response = new JSONObject(json);

            Log.d("", "----------------" + response.toString());

            int result_code = response.getInt("result_code");
            String photoUrl = response.getString("result_msg");

            Log.d("photo url", "----------------" + photoUrl);

            if (result_code == 0){//success

                _imageLoader.get(photoUrl, ImageLoader.getImageListener
                        (ui_imv_profile, R.drawable.user_img, R.drawable.user_img));

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void showImagePicker() {

        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener galleryListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeGalleryAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.picker_title))
                .setPositiveButton(getString(R.string.from_camera), cameraListener)
                .setNeutralButton(getString(R.string.from_gallery), galleryListener)
                .setNegativeButton(getString(R.string.picker_cancel), cancelListener)
                .show();
    }

    // do take a photo from camera
    private void doTakePhotoAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // temp file path to use
        String url = "imate_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        _imageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);

        // there are some problems not to save image in special phone
        // add line
        intent.putExtra("return-date", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    // do take a photo from gallery
    private void doTakeGalleryAction() {

        // call gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }

    private void uploadProfile() {

        // if no profile image
        if(_photoPath.length() == 0) {
            return;
        }

        try {

            showProgress();

            File file = new File(_photoPath);

            // upload test with my localhost
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("email", "mighty3star27@gmail.com");
//
//            String url = "http://192.168.0.40/mytest/index.php/api/imageUpload";

            // upload to api.tieyouin.com/api/image/Upload
            String url = ReqConst.SERVER_URL + ReqConst.REQ_UPLOAD_IMAGE;
            Map<String, String> params = new HashMap<String, String>();
            params.put(ReqConst.USER_ID, Common.userID);
            params.put(ReqConst.IS_PROFILE_PIC, String.valueOf(true));

            Map<String, String> hParams = new HashMap<String, String>();
            hParams.put(ReqConst.AUTHORIZATION, Common.token_type + " " + Common.access_token);

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    Log.d("upload_error", "----------------------" + volleyError.toString());
                    closeProgress();
                }

            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    parseUploadImgResponse(json);

                }
            }, file, "upload", params, hParams);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.VOLLEY_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            iMateApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();
            showAlertDialog(getString(R.string.photo_upload_fail));
        }
    }

    private void parseUploadImgResponse(String json) {

        closeProgress();

        Log.d("upload response:", "--------------" + json);

        showToast(getString(R.string.photo_upload_success));

//        try{
//            JSONObject response = new JSONObject(json);
//            int result_code = response.getInt("result_code");
//            String result_msg = response.getString("result_msg");
//
//            Log.d("msg", "---------------" + "result_msg");
//
//            if (result_code == 0){
//                showAlertDialog(getString(R.string.photo_upload_success));
//            }
//            else if(result_code == 200){
//                showAlertDialog(getString(R.string.photo_over_size));
//            }
//        }catch (JSONException e){
//
//            e.printStackTrace();
//
//        }
    }

    // for test
    private void gotoLogin() {
		UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_IS_LOGIN, false);
		UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_IS_REGISTER, false);

        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case CROP_FROM_CAMERA:

                // after croping, then receive image file
                // show image on imageview or after some working, then delete temp file
				if(resultCode == Activity.RESULT_OK) {

					try {

						File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory
                                .decodeStream(in, null, bitOpt);
                        in.close();

                        // set bitmap data to ImageView for profile
                        ui_imv_profile.setImageBitmap(bitmap);

                        _photoPath = saveFile.getAbsolutePath();

                        uploadProfile();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
                break;

            case PICK_FROM_GALLERY: {

                _imageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA: {

                try {

                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(_imageCaptureUri, "image/*");

                    intent.putExtra("crop", true);
                    intent.putExtra("scale", true);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);

                    intent.putExtra("outputX", Constant.PROFILE_IMAGE_SIZE);
                    intent.putExtra("outputY", Constant.PROFILE_IMAGE_SIZE);

                    intent.putExtra("noFaceDetection", true);
//                    intent.putExtra("return-data", true);

                    intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                    startActivityForResult(intent, CROP_FROM_CAMERA);

                } catch (Exception e) {

                    e.printStackTrace();
                }

                break;
            }
        }
    }

}
