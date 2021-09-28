package mx.tecnm.tepic.ladm_u1_practica2_archivosenmeroria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Environment
import java.io.*
import android.widget.Toast





var contenido = "";
var tituloNota = "";
var vectorCamposTitulos = ArrayList<String>()
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

       Thread.sleep(2000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         botonCrear.setOnClickListener {
             crearNueva()
         }

        botonAbrir.setOnClickListener {
            abrirArchivo()
        }

        botonEliminar.setOnClickListener {
            borrarArchivoInterno()
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
            .setPositiveButton("GUARDAR INTERNO") {d, i ->

                if( validaGuardar()){
                    Toast.makeText(this,"SE GUARDO CON EXITO",Toast.LENGTH_LONG).show()
                    crearRenglonVista(nombreNota,descripcion.text.toString())
                }
                else {
                    Toast.makeText(this,"NO SE GUARDO ARCHIVO",Toast.LENGTH_LONG).show()
                }
                d.dismiss()



            }
            .setNegativeButton("GUARDAR EXTERNO") {d, i ->
                guardarMemoriaExterna(nombreNota,descripcion.text.toString())
                d.dismiss()
            }
            .show()
    }
    fun crearRenglonVista(nombreNota: String, descripcion:String){

        val txtNota = TextView(this)
        val txtDescripcion =TextView(this)
        txtDescripcion.setPadding(0,0,0,20)
        txtNota.setText(nombreNota.uppercase())
        txtDescripcion.setText(descripcion)

        vectorCamposTitulos.add(txtNota.text.toString().uppercase())
        vectorCamposTitulos.add(txtNota.text.toString().uppercase())

        blocNotas.addView(txtNota)
        blocNotas.addView(txtDescripcion)

    }

    fun validaGuardar():Boolean{

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

    fun abrirArchivo(){
        val archivoAbierto = EditText(this)
        archivoAbierto.setHint("Archivo a abrir")

        AlertDialog.Builder(this)
            .setTitle("ABRIR NOTA")
            .setMessage("Escribe titulo de tu nota")
            .setView(archivoAbierto)
            .setPositiveButton("ABRIR") {d, i ->

                val tarjeta = getExternalFilesDir(null);
                val file = File(tarjeta?.absolutePath,archivoAbierto.text.toString().uppercase() )
                try {
                    val fIn = FileInputStream(file)
                    val archivo = InputStreamReader(fIn)
                    val br = BufferedReader(archivo)
                    var linea = br.readLine()
                    val todo = StringBuilder()
                    while (linea != null) {
                        todo.append(linea + "\n")
                        linea = br.readLine()
                    }
                    br.close()
                    archivo.close()
                    AlertDialog.Builder(this)
                        .setTitle("RECORDATORIO")
                        .setMessage(todo)
                        .setNegativeButton("ACEPTAR"){d,i ->
                            d.dismiss()
                        }
                        .show()


                } catch (e: IOException) {
                    Toast.makeText(this, "ARCHIVO NO ENCONTRADO", Toast.LENGTH_SHORT).show()
                }
            }


            .setNegativeButton("CANCELAR") {d, i ->
                d.cancel()
            }
            .show()

    }

    fun borrarArchivoInterno(){
        val archivoEliminar = EditText(this)
        archivoEliminar.setHint("Archivo a eliminar")

        AlertDialog.Builder(this)
            .setTitle("ELIMINAR NOTA")
            .setMessage("Escribe Titulo de Nota a Eliminar")
            .setView(archivoEliminar)
            .setPositiveButton("ELIMINAR A. EXTERNO") {d, i ->
                var tope = vectorCamposTitulos.size
                tope = tope-1
                (0..tope-1).forEach{
                   var titulo = vectorCamposTitulos.get(it).uppercase()

                    if(titulo.equals(archivoEliminar.text.toString().uppercase())){
                        var ind = vectorCamposTitulos.indexOf(archivoEliminar.text.toString().uppercase())
                      blocNotas.removeViews(ind+0, 2)
                       vectorCamposTitulos.removeAt(ind)
                        eliminarDeMemoria(archivoEliminar.text.toString())
                        d.dismiss()
                    }

                }
                eliminarDeMemoria(archivoEliminar.text.toString())

            }
            .setNegativeButton("ELIMINAR A. INTERNO") {d, i ->
                eliminarArchivoInterno()
            }
            .show()

    }

    fun guardarMemoriaExterna(nombreNota: String, descripcion:String){
        try {
            val tarjeta = getExternalFilesDir(null);
            val file = File(tarjeta?.getAbsolutePath(), nombreNota.uppercase())
            val osw = OutputStreamWriter(FileOutputStream(file))
            osw.write(descripcion)
            osw.flush()
            osw.close()
            Toast.makeText(this, "DATOS ALMACENADOS CORRECTAMENTE", Toast.LENGTH_LONG).show()
            crearRenglonVista(nombreNota.uppercase(),descripcion)
        } catch (ioe: IOException) {
            Toast.makeText(this, "ERROR DE ALMACENAMIENTO",Toast.LENGTH_LONG).show()
        }
    }

    fun eliminarDeMemoria(nombreEliminar : String){
        try {
            val sdCard = getExternalFilesDir(null);

            val dir = File(sdCard?.absolutePath.toString())
            if (dir.isDirectory) {
                File(dir,"$nombreEliminar").delete()
                Toast.makeText(this, "ELIMINADO CORRECTAMENTE", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "ARCHIVO NO EXISTENTE", Toast.LENGTH_LONG).show()
            }

        } catch (ioe: IOException) {
            Toast.makeText(this, "ERROR ",Toast.LENGTH_LONG).show()
        }
    }

    fun eliminarArchivoInterno(){
            val archivoEliminar = EditText(this)
            archivoEliminar.setHint("Archivo a eliminar")
            var notaEliminar = ""

            AlertDialog.Builder(this)
                .setTitle("ELIMINAR NOTA INTERNA")
                .setMessage("Escribe indice de nota a eliminar")
                .setView(archivoEliminar)
                .setPositiveButton("ELIMINAR") {d, i ->

                    try {
                        if(archivoEliminar.text.toString().toInt() == 1 ) {
                            blocNotas.removeViews(0, 2)
                            Toast.makeText(this, "ARCHIVO ELIMINADO", Toast.LENGTH_LONG).show()
                            notaEliminar = vectorCamposTitulos[0].toString()
                            eliminarDeMemoria(notaEliminar)
                        }
                        else {
                            blocNotas.removeViews(archivoEliminar.text.toString().toInt(), 2)
                            Toast.makeText(this, "ARCHIVO ELIMINADO", Toast.LENGTH_LONG).show()
                            notaEliminar = vectorCamposTitulos[archivoEliminar.text.toString().toInt()]
                            eliminarDeMemoria(notaEliminar)
                        }
                    } catch (ioe: IOException) {
                        Toast.makeText(this, "ERROR DE ELIMINACION" ,Toast.LENGTH_LONG).show()
                    }

                }
                .setNegativeButton("CANCELAR") {d, i ->
                    d.cancel()
                }
                .show()

    }
}