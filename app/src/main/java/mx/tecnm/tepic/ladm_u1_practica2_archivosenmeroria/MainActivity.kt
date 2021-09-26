package mx.tecnm.tepic.ladm_u1_practica2_archivosenmeroria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.OutputStreamWriter

var contenido = "";
var tituloNota = "";
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         botonCrear.setOnClickListener {
             crearNueva()
         }
    }

    fun crearNueva(){
        val titulo = EditText(this)
        titulo.setHint("TITULO")

        AlertDialog.Builder(this)
            .setTitle("NUEVA NOTA")
            .setMessage("Escribe titulo de tu nueva nota")
            .setView(titulo)
            .setPositiveButton("ACEPTAR") {d, i ->
                generarDescripcion(titulo.text.toString())
                d.dismiss()
            }
            .setNegativeButton("CANCELAR") {d, i ->
                d.cancel()
            }
            .show()
    }

    fun generarDescripcion(nombreNota : String){
        val descripcion = EditText(this)
        descripcion.setHint("DESCRIPCION")
        contenido = descripcion.text.toString()
        tituloNota = nombreNota

        AlertDialog.Builder(this)
            .setTitle("DESCRIPCION NOTA")
            .setMessage("Escribe contenido de ${nombreNota}" )
            .setView(descripcion)
            .setPositiveButton("GUARDAR M.I") {d, i ->
                d.dismiss()
                if( validaGuardar()){
                    Toast.makeText(this,"SE GUARDO CON EXITO",Toast.LENGTH_LONG).show()
                    crearRenglonVista(nombreNota.uppercase(),descripcion.text.toString())
                }
                else {
                    Toast.makeText(this,"NO SE GUARDO ARCHIVO",Toast.LENGTH_LONG).show()
                }




            }
            .setNegativeButton("GUARDAR M.E") {d, i ->
                d.dismiss()
            }
            .show()
    }
    fun crearRenglonVista(nombreNota: String, descripcion:String){

        val txtNota = TextView(this)
        txtNota.setPadding(0,0,0,30)
        txtNota.setText(nombreNota+"\n"+descripcion)


        blocNotas.addView(txtNota)

    }

    fun validaGuardar():Boolean{
        val nombreArchivo =
        try {
            val archivo = OutputStreamWriter(openFileOutput("$tituloNota"+".txt", MODE_PRIVATE))
            var dataContenido = contenido

            archivo.write(dataContenido)
            archivo.flush() // obliga al dispositivo a almacenar en memoria flash (interno)
            archivo.close() // cierra el archivo para evitar que se altere el archivo
            return  true;
        }catch (io: IOException){
            AlertDialog.Builder(this)
                .setTitle("ATENCION!, ERROR")
                .setMessage(io.message)
                .setPositiveButton("ACEPTAR") {dialog,exception ->
                    dialog.dismiss()
                }
                .show()
            return false;
        }
    }
}