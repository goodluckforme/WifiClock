package xiaomakj.wificlock.com.mvp.ui.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_user_center.*
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerUSerCenterComponent
import xiaomakj.wificlock.com.databinding.ActivityUserCenterBinding
import xiaomakj.wificlock.com.mvp.contract.UserCenterContract
import xiaomakj.wificlock.com.mvp.presenter.USerCenterPresenter
import xiaomakj.wificlock.com.mvp.presenter.USerCenterPresenter.Companion.RC_CHOOSE_PHOTO
import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.utils.GlideCircleTransform
import java.io.File


class UserCenterActivity : BaseActivity<USerCenterPresenter, UserCenterContract.View, ActivityUserCenterBinding>(), UserCenterContract.View {
    override fun getNameId(): Int = R.string.usercenter

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerUSerCenterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityUserCenterBinding>(this@UserCenterActivity, R.layout.activity_user_center))
        super.initView()
    }

    public override fun initData() {
        super.initData()
        mPresenter.attachView(this)
        mPresenter.toInit()
    }

    override fun complete(msg: String) {

    }

    override fun showError(msg: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            val selectedImages = BGAPhotoPickerActivity.getSelectedImages(data) ?: return
            val selectedPhotos = selectedImages[0]
            val file = File(selectedPhotos)
            if (!file.exists())return
            mPresenter.uploadHeadImg(file)
        }
    }
}
