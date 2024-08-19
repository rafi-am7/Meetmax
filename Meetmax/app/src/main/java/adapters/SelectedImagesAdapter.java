package adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.meetmax.R;

import java.util.ArrayList;

public class SelectedImagesAdapter extends PagerAdapter {
    private Context context;
    ArrayList<Uri>imagesList;
    LayoutInflater layoutInflater;

    public SelectedImagesAdapter(Context context, ArrayList<Uri> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.show_image_layout,container,false);
        ImageView selectedImageView = view.findViewById(R.id.loaded_from_gallery_imageview);
        selectedImageView.setImageURI(imagesList.get(position));
        container.addView(view);
        return  view;
        //////////////////
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
