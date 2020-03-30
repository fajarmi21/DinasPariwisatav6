package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.app.ProgressDialog
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SuperFilterExcel {
    fun excel(c: Context, ar: ArrayList<ArrayList<Any>>) {
        val progressDialog = ProgressDialog(c)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating....")
        progressDialog.show()
        try {
            val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYY"))
            val time2 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHms"))
            val fileName = "SuperAdmin$time2.xlsx"
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
            val folder = File(extStorageDirectory, "Disbudparpora/$time")
            folder.mkdirs()
            val file = File(folder, fileName)
            try {
                file.createNewFile()
            } catch (e : Exception) {
                progressDialog.hide()
                Toast.makeText(c, "Downloading gagal", Toast.LENGTH_SHORT).show()
                Log.e("e", e.message)
            }
            val fileOutputStream = FileOutputStream(file)

            val wb = XSSFWorkbook()
            val sh = wb.createSheet("sheet1")

            sh.setColumnWidth(1, 4*256)
            sh.setColumnWidth(5, 10*256)

            try {
                val f = wb.createFont()
                val c = wb.createCellStyle()
                sh.addMergedRegion(CellRangeAddress.valueOf("B2:F3"))
                val font = f.apply {
                    fontName = "Courier New"
                    italic = false
                    bold = true
                    underline = Font.U_SINGLE
                }
                val style = c.apply {
                    alignment = CellStyle.ALIGN_CENTER
                    wrapText = true
                    setFont(font)
                }
                val kop = sh.createRow(1).createCell(1)
                kop.cellStyle = style
                kop.setCellValue("Dinas Kebudayaan, Pariwisata, Kepemudaan dan Olahraga Kota Kediri")
                Log.e("suksesH", "excell")
            } catch (e: Exception) {
                progressDialog.hide()
                Toast.makeText(c, "Downloading gagal", Toast.LENGTH_SHORT).show()
                Log.e("header", e.message)
            }

            try {
                var ls = 0
                var tpg = 1
                var tpd = 1
                try {
                    sh.addMergedRegion(CellRangeAddress.valueOf("B5:B6"))
                    sh.addMergedRegion(CellRangeAddress.valueOf("C5:E5"))
                    sh.addMergedRegion(CellRangeAddress.valueOf("F5:F6"))

                    for (x in 4..5) {
                        val z = sh.createRow(x)
                        for (y in 1..5) {
                            val cs = wb.createCellStyle()
                            val v = z.createCell(y)
                            cs.borderLeft = CellStyle.BORDER_THIN
                            cs.borderTop = CellStyle.BORDER_THIN
                            cs.borderRight = CellStyle.BORDER_THIN
                            cs.borderBottom = CellStyle.BORDER_THIN
                            v.cellStyle = cs
                            when {
                                y == 1 && x == 4 -> {
                                    v.setCellValue("No")
                                    cs.alignment = CellStyle.ALIGN_CENTER
                                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                                    v.cellStyle = cs
                                }
                                y == 2 && x == 4 -> {
                                    v.setCellValue("Pengunjung")
                                    cs.alignment = CellStyle.ALIGN_CENTER
                                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                                    v.cellStyle = cs
                                }
                                y == 5 && x == 4 -> {
                                    v.setCellValue("Pemasukan")
                                    cs.alignment = CellStyle.ALIGN_CENTER
                                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                                    v.cellStyle = cs
                                }
                                y == 2 && x == 5 -> {
                                    v.setCellValue("Local")
                                    cs.alignment = CellStyle.ALIGN_CENTER
                                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                                    v.cellStyle = cs
                                }
                                y == 3 && x == 5 -> {
                                    v.setCellValue("Manca")
                                    cs.alignment = CellStyle.ALIGN_CENTER
                                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                                    v.cellStyle = cs
                                }
                                y == 4 && x == 5 -> {
                                    v.setCellValue("Total")
                                    cs.alignment = CellStyle.ALIGN_CENTER
                                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                                    v.cellStyle = cs
                                }
                            }
                        }
                    }
                    Log.e("suksesCH", "excell")
                } catch (e: Exception) {
                    progressDialog.hide()
                    Toast.makeText(c, "Downloading gagal", Toast.LENGTH_SHORT).show()
                    Log.e("contentH", e.toString())
                }


                if (ar.size != 0) {
                    try {
                        var t1 = 0
                        var t2 = 0
                        var b = 6
                            ar.forEach { it1 ->
                                var w = 0
                                it1.forEach {
                                    when(it) {
                                        is String -> {
                                            w = 0
                                            sh.addMergedRegion(CellRangeAddress.valueOf("B${b+1}:F${b+1}"))
                                            val r = sh.createRow(b).createCell(1)
                                            r.setCellValue(" $it")
                                            r.cellStyle = wb.createCellStyle().apply { setFont(wb.createFont().apply { italic = true }) }
                                            RegionUtil.setBorderLeft(1,CellRangeAddress.valueOf("B${b+1}:F${b+1}"),sh,wb)
                                            RegionUtil.setBorderTop(1,CellRangeAddress.valueOf("B${b+1}:F${b+1}"),sh,wb)
                                            RegionUtil.setBorderRight(1,CellRangeAddress.valueOf("B${b+1}:F${b+1}"),sh,wb)
                                            RegionUtil.setBorderBottom(1,CellRangeAddress.valueOf("B${b+1}:F${b+1}"),sh,wb)
                                        }
                                        is itemP -> {
                                            w++
                                            val r = sh.createRow(b)
                                            for (i in 1..5) {
                                                val c = r.createCell(i)
                                                val cs = wb.createCellStyle()
                                                cs.borderLeft = CellStyle.BORDER_THIN
                                                cs.borderTop = CellStyle.BORDER_THIN
                                                cs.borderRight = CellStyle.BORDER_THIN
                                                cs.borderBottom = CellStyle.BORDER_THIN
                                                when(i) {
                                                    1 -> {
                                                        c.setCellValue("$w")
                                                        cs.alignment = CellStyle.ALIGN_CENTER
                                                        c.cellStyle = cs
                                                    }
                                                    2 -> {
                                                        c.setCellValue("${it.pgD}")
                                                        c.cellStyle = cs
                                                    }
                                                    3 -> {
                                                        c.setCellValue("${it.pgL}")
                                                        c.cellStyle = cs
                                                    }
                                                    4 -> {
                                                        c.setCellValue("${it.pgT}")
                                                        c.cellStyle = cs
                                                        t1 += it.pgT.toInt()
                                                    }
                                                    5 -> {
                                                        c.setCellValue("Rp${it.pgL}")
                                                        c.cellStyle = cs
                                                        t2 += it.pgL.toInt()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    b++
                                }
                                ls = b+1
                            }
                        tpg = t1
                        tpd = t2
                        Log.e("suksesCC", "excell")
                    } catch (e: Exception) {
                        progressDialog.hide()
                        Toast.makeText(c, "Downloading gagal", Toast.LENGTH_SHORT).show()
                        Log.e("contentC", e.toString())
                    }

                    try {
                        sh.addMergedRegion(CellRangeAddress.valueOf("B$ls:D$ls"))

                        val r = sh.createRow(ls-1)
                        val v = r.createCell(1)
                        val cs = wb.createCellStyle()
                        val f = wb.createFont().apply { italic = true }
                        v.setCellValue("Total  ")
                        cs.alignment = CellStyle.ALIGN_RIGHT
                        cs.borderLeft = CellStyle.BORDER_THIN
                        cs.borderTop = CellStyle.BORDER_THIN
                        cs.borderRight = CellStyle.BORDER_THIN
                        cs.setFont(f)
                        v.cellStyle = cs

                        for (i in 4..5) {
                            val vi = r.createCell(i)
                            val ca = wb.createCellStyle()
                            ca.borderLeft = CellStyle.BORDER_THIN
                            ca.borderTop = CellStyle.BORDER_THIN
                            ca.borderRight = CellStyle.BORDER_THIN
                            when(i) {
                                4 -> {
                                    vi.setCellValue("$tpg")
                                    vi.cellStyle = ca
                                }
                                5 -> {
                                    vi.setCellValue("Rp$tpd")
                                    vi.cellStyle = ca
                                }
                            }
                        }
                        RegionUtil.setBorderBottom(1,CellRangeAddress.valueOf("B$ls:F$ls"),sh,wb)
                        Log.e("suksesCF", "excell")
                    } catch (e: Exception) {
                        progressDialog.hide()
                        Toast.makeText(c, "Downloading gagal", Toast.LENGTH_SHORT).show()
                        Log.e("contentF", e.toString())
                    }
                } else {
                    sh.addMergedRegion(CellRangeAddress.valueOf("B7:F7"))
                    val cs = wb.createCellStyle()
                    val c = sh.createRow(6).createCell(6)
                    c.setCellValue("Data Kosong")
                    cs.alignment = CellStyle.ALIGN_CENTER
                    cs.verticalAlignment = CellStyle.VERTICAL_CENTER
                    cs.setFont(wb.createFont().apply { italic = true })
                    c.cellStyle = cs
                    RegionUtil.setBorderLeft(1,CellRangeAddress.valueOf("B7:F7"),sh,wb)
                    RegionUtil.setBorderTop(1,CellRangeAddress.valueOf("B7:F7"),sh,wb)
                    RegionUtil.setBorderRight(1,CellRangeAddress.valueOf("B7:F7"),sh,wb)
                    RegionUtil.setBorderBottom(1,CellRangeAddress.valueOf("B7:F7"),sh,wb)
                }
                Log.e("suksesC", "excell")
            } catch (e: Exception) {
                progressDialog.hide()
                Toast.makeText(c, "Downloading gagal", Toast.LENGTH_SHORT).show()
                Log.e("content", e.toString())
            }

            wb.write(fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) { Log.e("file", e.message) }
        Toast.makeText(c, "Downloading sukses", Toast.LENGTH_SHORT).show()
        progressDialog.hide()
    }

    class itemP(val no: String?, val pgD: String, val pgL: String, val pgT: String = (pgD.toInt() + pgL.toInt()).toString())
}