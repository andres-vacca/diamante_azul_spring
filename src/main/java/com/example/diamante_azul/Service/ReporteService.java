package com.example.diamante_azul.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Imports de Apache POI (Excel)
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

// Imports de iText (PDF)
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Models.Cuotas;
import com.example.diamante_azul.Models.Empeno;
import com.example.diamante_azul.Models.Producto; // Importar Producto

@Service
public class ReporteService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =========================================================================
    //              1. REPORTE INDIVIDUAL (PARA EL CLIENTE / USUARIO)
    // =========================================================================

    public byte[] generarReporteExcel(Usuario usuario, List<Empeno> empenos, List<Cuotas> cuotas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // --- ESTILOS ---
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);

            // --- HOJA 1: INFORMACIÓN DEL CLIENTE ---
            Sheet sheetCliente = workbook.createSheet("Mi Información");
            int rowNum = 0;

            // Título
            Row titleRow = sheetCliente.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("ESTADO DE CUENTA - DIAMANTE AZUL");
            titleCell.setCellStyle(titleStyle);
            sheetCliente.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            rowNum++;

            // Datos del Usuario
            crearFilaInfo(sheetCliente, rowNum++, "Nombre:", usuario.getNombre());
            crearFilaInfo(sheetCliente, rowNum++, "Documento:", usuario.getDocumento());
            crearFilaInfo(sheetCliente, rowNum++, "Email:", usuario.getEmail());
            crearFilaInfo(sheetCliente, rowNum++, "Teléfono:", usuario.getTelefono());
            crearFilaInfo(sheetCliente, rowNum++, "Dirección:", usuario.getDireccion());

            sheetCliente.autoSizeColumn(0);
            sheetCliente.autoSizeColumn(1);

            // --- HOJA 2: MIS EMPEÑOS ---
            Sheet sheetEmpenos = workbook.createSheet("Mis Empeños");
            rowNum = 0;

            // Encabezados
            Row headerEmp = sheetEmpenos.createRow(rowNum++);
            String[] colEmp = {"ID", "Producto", "Monto", "Tasa %", "Fecha", "Vence", "Estado"};
            for(int i=0; i<colEmp.length; i++) {
                Cell cell = headerEmp.createCell(i);
                cell.setCellValue(colEmp[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            for (Empeno e : empenos) {
                Row row = sheetEmpenos.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getId());
                // Asegurar que Producto no sea nulo, aunque Hibernate Proxy debería manejarlo
                row.createCell(1).setCellValue(e.getProducto() != null ? e.getProducto().getNombre() : "N/A");
                row.createCell(2).setCellValue(e.getMontoPrestado().doubleValue());
                row.createCell(3).setCellValue(e.getTasaInteres().doubleValue());
                row.createCell(4).setCellValue(e.getFechaEmpeno().format(DATE_FORMATTER));
                row.createCell(5).setCellValue(e.getFechaVencimiento().format(DATE_FORMATTER));
                row.createCell(6).setCellValue(e.getEstadoEmpeno());
            }
            for(int i=0; i<colEmp.length; i++) sheetEmpenos.autoSizeColumn(i);

            // --- HOJA 3: MIS CUOTAS ---
            Sheet sheetCuotas = workbook.createSheet("Mis Cuotas");
            rowNum = 0;

            Row headerCuota = sheetCuotas.createRow(rowNum++);
            String[] colCuota = {"Ref. Empeño", "N° Cuota", "Valor", "Vence", "Pagado", "Estado"};
            for(int i=0; i<colCuota.length; i++) {
                Cell cell = headerCuota.createCell(i);
                cell.setCellValue(colCuota[i]);
                cell.setCellStyle(headerStyle);
            }

            for (Cuotas c : cuotas) {
                Row row = sheetCuotas.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getEmpeno() != null ? c.getEmpeno().getId() : 0);
                row.createCell(1).setCellValue(c.getNumeroCuota());
                row.createCell(2).setCellValue(c.getValorCuota().doubleValue());
                row.createCell(3).setCellValue(c.getFechaVencimiento().format(DATE_FORMATTER));
                row.createCell(4).setCellValue(c.getFechaPago() != null ? c.getFechaPago().format(DATE_FORMATTER) : "-");
                row.createCell(5).setCellValue(c.getEstadoCuota());
            }
            for(int i=0; i<colCuota.length; i++) sheetCuotas.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generarReportePDF(Usuario usuario, List<Empeno> empenos, List<Cuotas> cuotas) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Título
            document.add(new Paragraph("ESTADO DE CUENTA - DIAMANTE AZUL")
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\nDATOS DEL CLIENTE").setBold());
            document.add(new Paragraph("Nombre: " + usuario.getNombre()));
            document.add(new Paragraph("Documento: " + usuario.getDocumento()));
            document.add(new Paragraph("Fecha Emisión: " + LocalDate.now().format(DATE_FORMATTER)));
            document.add(new Paragraph("\n"));

            // Tabla Empeños
            document.add(new Paragraph("MIS EMPEÑOS").setBold().setFontSize(14));
            Table tableEmp = new Table(UnitValue.createPercentArray(new float[]{1,3,2,2,2})).useAllAvailableWidth();
            tableEmp.addHeaderCell("ID");
            tableEmp.addHeaderCell("Producto");
            tableEmp.addHeaderCell("Monto");
            tableEmp.addHeaderCell("Vence");
            tableEmp.addHeaderCell("Estado");

            for (Empeno e : empenos) {
                tableEmp.addCell(String.valueOf(e.getId()));
                tableEmp.addCell(e.getProducto() != null ? e.getProducto().getNombre() : "N/A");
                tableEmp.addCell("$" + e.getMontoPrestado());
                tableEmp.addCell(e.getFechaVencimiento().format(DATE_FORMATTER));
                tableEmp.addCell(e.getEstadoEmpeno());
            }
            document.add(tableEmp);

            document.add(new Paragraph("\n"));

            // Tabla Cuotas
            document.add(new Paragraph("MIS CUOTAS PENDIENTES/PAGADAS").setBold().setFontSize(14));
            Table tableCuota = new Table(UnitValue.createPercentArray(new float[]{2,1,2,2,2})).useAllAvailableWidth();
            tableCuota.addHeaderCell("Ref. Empeño");
            tableCuota.addHeaderCell("#");
            tableCuota.addHeaderCell("Valor");
            tableCuota.addHeaderCell("Vence");
            tableCuota.addHeaderCell("Estado");

            for (Cuotas c : cuotas) {
                tableCuota.addCell(c.getEmpeno() != null ? "EMP-" + c.getEmpeno().getId() : "N/A");
                tableCuota.addCell(String.valueOf(c.getNumeroCuota()));
                tableCuota.addCell("$" + c.getValorCuota());
                tableCuota.addCell(c.getFechaVencimiento().format(DATE_FORMATTER));
                tableCuota.addCell(c.getEstadoCuota());
            }
            document.add(tableCuota);
        }
        return out.toByteArray();
    }

    // =========================================================================
    //              2. REPORTES GLOBALES (PARA EL ADMINISTRADOR)
    // =========================================================================

    public byte[] generarReporteGlobalExcel(List<Usuario> usuarios, List<Empeno> empenos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = createHeaderStyle(workbook);

            // --- HOJA 1: TODOS LOS EMPEÑOS ---
            Sheet sheetEmp = workbook.createSheet("Empeños Globales");
            String[] headE = {"ID", "Cliente", "Doc. Cliente", "Producto", "Monto", "Fecha", "Vence", "Estado"};
            Row rowHE = sheetEmp.createRow(0);
            for(int i=0; i<headE.length; i++) {
                Cell c = rowHE.createCell(i);
                c.setCellValue(headE[i]);
                c.setCellStyle(headerStyle);
            }

            int r = 1;
            for(Empeno e : empenos) {
                Row row = sheetEmp.createRow(r++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getCliente() != null ? e.getCliente().getNombre() : "N/A");
                row.createCell(2).setCellValue(e.getCliente() != null ? e.getCliente().getDocumento() : "N/A");
                row.createCell(3).setCellValue(e.getProducto() != null ? e.getProducto().getNombre() : "N/A");
                row.createCell(4).setCellValue(e.getMontoPrestado().doubleValue());
                row.createCell(5).setCellValue(e.getFechaEmpeno().format(DATE_FORMATTER));
                row.createCell(6).setCellValue(e.getFechaVencimiento().format(DATE_FORMATTER));
                row.createCell(7).setCellValue(e.getEstadoEmpeno());
            }
            for(int i=0; i<headE.length; i++) sheetEmp.autoSizeColumn(i);

            // --- HOJA 2: TODOS LOS USUARIOS ---
            Sheet sheetUs = workbook.createSheet("Usuarios Sistema");
            String[] headU = {"ID", "Nombre", "Email", "Documento", "Rol", "Estado"};
            Row rowHU = sheetUs.createRow(0);
            for(int i=0; i<headU.length; i++) {
                Cell c = rowHU.createCell(i);
                c.setCellValue(headU[i]);
                c.setCellStyle(headerStyle);
            }

            r = 1;
            for(Usuario u : usuarios) {
                Row row = sheetUs.createRow(r++);
                row.createCell(0).setCellValue(u.getId());
                row.createCell(1).setCellValue(u.getNombre());
                row.createCell(2).setCellValue(u.getEmail());
                row.createCell(3).setCellValue(u.getDocumento());
                row.createCell(4).setCellValue(u.getRol() != null ? u.getRol().getNombre() : "N/A");
                row.createCell(5).setCellValue(u.getEstadoUsuario());
            }
            for(int i=0; i<headU.length; i++) sheetUs.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * CORREGIDO: Este método se usa en el controlador para reportes vencidos.
     * Genera un PDF simple de Empeños (usado para mostrar vencidos).
     */
    public byte[] generarReporteGlobalPDF(List<Empeno> empenos) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            document.add(new Paragraph("REPORTE GENERAL DE EMPEÑOS")
                    .setFontSize(16).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DATE_FORMATTER))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("\n"));

            if (empenos.isEmpty()) {
                document.add(new Paragraph("No hay registros de empeños para este reporte.").setTextAlignment(TextAlignment.CENTER));
                pdfDoc.close();
                return out.toByteArray();
            }

            float[] colWidths = {1, 3, 3, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();

            table.addHeaderCell("ID");
            table.addHeaderCell("Cliente");
            table.addHeaderCell("Producto");
            table.addHeaderCell("Monto");
            table.addHeaderCell("Vence");
            table.addHeaderCell("Estado");

            for(Empeno e : empenos) {
                table.addCell(String.valueOf(e.getId()));
                table.addCell(e.getCliente() != null ? e.getCliente().getNombre() : "N/A");
                table.addCell(e.getProducto() != null ? e.getProducto().getNombre() : "N/A");
                table.addCell("$" + e.getMontoPrestado());
                table.addCell(e.getFechaVencimiento().format(DATE_FORMATTER));
                table.addCell(e.getEstadoEmpeno());
            }

            document.add(table);
            document.add(new Paragraph("\nTotal Registros: " + empenos.size()));
        }
        return out.toByteArray();
    }

    // =========================================================================
    //              3. REPORTE DE INVENTARIO (CSV) - Agregado
    // =========================================================================

    /**
     * Método necesario para la ruta /dashboard/reportes/inventario.
     * Genera la lista de productos en formato CSV.
     */
    public byte[] generarReporteInventarioCSV(List<Producto> listaProductos) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintWriter writer = new PrintWriter(out)) {

            // CABECERA CSV
            writer.println("ID,Nombre,Descripcion,Precio,Estado");

            // FILAS DE DATOS
            for (Producto producto : listaProductos) {
                // Usamos el getter getEstado() de tu entidad Producto
                String estado = producto.getEstado() != null ? producto.getEstado() : "N/A";

                String linea = String.format("%d,\"%s\",\"%s\",%.2f,%s",
                        producto.getId(),
                        producto.getNombre().replace("\"", "\"\""), // Escapar comillas
                        producto.getDescripcion().replace("\"", "\"\""), // Escapar comillas
                        producto.getPrecio().doubleValue(),
                        estado
                );
                writer.println(linea);
            }
            writer.flush();
            return out.toByteArray();
        }
    }

    // =========================================================================
    //              MÉTODOS AUXILIARES (ESTILOS)
    // =========================================================================

    private void crearFilaInfo(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);

        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        boldStyle.setFont(boldFont);
        labelCell.setCellStyle(boldStyle);

        row.createCell(1).setCellValue(value != null ? value : "N/A");
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}