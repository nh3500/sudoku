package org.isi.sudoku.utility;

import org.isi.sudoku.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToastHelper {

	public static Toast toastWithoutOverlap(Context context, Toast toast, String message) {
		
		if (toast != null) {
			
			toast.cancel();
			toast.setText(message);
		} else {

			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}
		return toast;
	}
	
	public static Toast customToast(Activity activity, Toast toast, int imageId, String message) {
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.customtoast, (ViewGroup) activity.findViewById(R.id.toastLayout));
		ImageView image = (ImageView) layout.findViewById(R.id.toastImage);
		image.setImageResource(imageId);
		TextView text = (TextView) layout.findViewById(R.id.toastText);
		text.setText(message);
		
		Toast customToast = new Toast(activity.getApplicationContext());
		customToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 220);
		customToast.setView(layout);
		customToast.setDuration(3000);
		
		return customToast;
	}
}
