package com.example.templechen.videoshaderdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.templechen.videoshaderdemo.album.AlbumActivity
import com.example.templechen.videoshaderdemo.cpp.NativeSurfaceActivity
import com.example.templechen.videoshaderdemo.gl.SimpleGLActivity
import com.example.templechen.videoshaderdemo.gl.gesture.GestureActivity
import com.example.templechen.videoshaderdemo.gl.sticker.StickerActivity
import com.example.templechen.videoshaderdemo.image.ImageSurfaceActivity
import com.example.templechen.videoshaderdemo.multiple.FakeSurfaceActivity
import com.example.templechen.videoshaderdemo.offscreen.OffScreenActivity
import com.example.templechen.videoshaderdemo.snaphelper.SnapHelperActivity
import com.example.templechen.videoshaderdemo.test.RecyclerViewTouchTestActivity
import com.example.templechen.videoshaderdemo.widget.coordinator.CoordinatorActivity
import kotlinx.android.synthetic.main.activity_prepare.*

class PrepareActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recordBtn: Button
    private lateinit var playBtn: Button
    private lateinit var stickerBtn: Button
    private lateinit var offScreenBtn: Button
    private lateinit var nativeSurfaceBtn: Button
    private lateinit var gestureSurfaceBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare)
        recordBtn = findViewById(R.id.RecordBtn)
        playBtn = findViewById(R.id.PlayingBtn)
        stickerBtn = findViewById(R.id.stickerBtn)
        recordBtn.setOnClickListener(this)
        playBtn.setOnClickListener(this)
        stickerBtn.setOnClickListener(this)
        offScreenBtn = findViewById(R.id.offScreenBtn)
        offScreenBtn.setOnClickListener(this)
        nativeSurfaceBtn = findViewById(R.id.nativeSurface)
        nativeSurfaceBtn.setOnClickListener(this)
        gestureSurfaceBtn = findViewById(R.id.gestureSurface)
        gestureSurfaceBtn.setOnClickListener(this)
        imageSurface.setOnClickListener(this)

        viewPager2.setOnClickListener(this)
        SnapHelper.setOnClickListener(this)
        test.setOnClickListener(this)
        album.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= 21 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                OffScreenActivity.PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == OffScreenActivity.PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "must need write external storage permission!", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.RecordBtn -> {
                val intent = Intent(this, SimpleGLActivity::class.java)
                startActivity(intent)
            }
            R.id.PlayingBtn -> {
                val intent = Intent(this, PurePlayActivity::class.java)
                startActivity(intent)
            }
            R.id.stickerBtn -> {
                val intent = Intent(this, StickerActivity::class.java)
                startActivity(intent)
            }
            R.id.offScreenBtn -> {
                val intent = Intent(this, OffScreenActivity::class.java)
                startActivity(intent)
            }
            R.id.nativeSurface -> {
                val intent = Intent(this, NativeSurfaceActivity::class.java)
                startActivity(intent)
            }
            R.id.gestureSurface -> {
                val intent = Intent(this, GestureActivity::class.java)
                startActivity(intent)
            }
            R.id.imageSurface -> {
                val intent = Intent(this, ImageSurfaceActivity::class.java)
                startActivity(intent)
            }
            R.id.viewPager2 -> {
                val intent = Intent(this, FakeSurfaceActivity::class.java)
                startActivity(intent)
            }
            R.id.SnapHelper -> {
                val intent = Intent(this, SnapHelperActivity::class.java)
                startActivity(intent)
            }
            R.id.album -> {
                val intent = Intent(this, AlbumActivity::class.java)
                startActivity(intent)
            }
            R.id.test -> {
                val intent = Intent(this, CoordinatorActivity::class.java)
                startActivity(intent)
            }
        }
    }

}