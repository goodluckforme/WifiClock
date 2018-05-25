package xiaomakj.wificlock.com.utils

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by Administrator on 2018/5/25.
 */
@BindingAdapter("bind:image")
fun ImageView.imageLoader(url: String) {
    Glide.with(context).load(url).into(this)
}