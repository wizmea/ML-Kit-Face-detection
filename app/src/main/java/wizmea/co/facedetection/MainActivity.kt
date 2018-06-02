package wizmea.co.facedetection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

typealias Param = ViewGroup.LayoutParams
class MainActivity : AppCompatActivity() {

    lateinit var imageView: ImageView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        imageView = findViewById(R.id.imageview)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            openCamera()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(21)
    private fun openCamera(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
            == PermissionChecker.PERMISSION_GRANTED){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,1000)

        }else {
            Toast.makeText(this,"Permission failed",Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK){
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            openFaceDetector(imageBitmap)
        }
    }

    private fun openFaceDetector(imageBitmap: Bitmap) {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.15f)
                .setTrackingEnabled(true)
                .build()
        val image = FirebaseVisionImage.fromBitmap(imageBitmap)


        val detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options)

        detector.detectInImage(image).addOnCompleteListener {
            faces -> run {
            for (face in faces.result){

                /*val rect = Rect(face.boundingBox)
                val paint = Paint()
                paint.color = Color.BLACK
                val canvas = Canvas()
                canvas.drawRect(face.boundingBox,paint)

                imageView.draw(canvas)*/

                imageView


                Toast.makeText(this,face.boundingBox.top.toString(),Toast.LENGTH_LONG).show()
                val emoji = ImageView(this@MainActivity)
                addContentView(emoji, ViewGroup.LayoutParams(Param.MATCH_PARENT,Param.MATCH_PARENT))
                //emoji.invalidate()
                emoji.setImageResource(R.drawable.happy)
                emoji.requestRectangleOnScreen(face.boundingBox,true)


            }
        }

        }.addOnFailureListener {
            Toast.makeText(this,"Fail",Toast.LENGTH_LONG).show()
        }
    }


}
