package Online.Utlity;

import Online.Entity.Order;
import Online.Entity.OrderItems;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfView{

    public ByteArrayOutputStream generatePdfFromOrders(List<Order> orders) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);

        document.open();

        int recordCount = 0;
        for (Order order : orders) {
            if (recordCount > 0 && recordCount % 10 == 0) {
                document.newPage();
            }
            addHeader(document);
            addOrderDetails(document, order);
            addFooter(document);
            recordCount++;
        }

        document.close();
        return outputStream;
    }

    private void addHeader(Document document) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell logoCell = new PdfPCell();
        ClassPathResource resource = new ClassPathResource("static/images/n.png");
        Image logo = Image.getInstance(resource.getURL());
        logo.scaleToFit(100f, 100f);
        logoCell.addElement(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(logoCell);

        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        invoiceCell.addElement(new Paragraph("Invoice", FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD)));
        headerTable.addCell(invoiceCell);

        document.add(headerTable);
    }

    private void addOrderDetails(Document document, Order order) throws DocumentException {
        PdfPTable orderTable = new PdfPTable(4);
        orderTable.setWidthPercentage(100);

        String[] headers = {"Product", "Price", "Quantity", "Total"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(Color.LIGHT_GRAY);
            orderTable.addCell(headerCell);
        }

        for (OrderItems orderItem : order.getOrderItems()) {
            orderTable.addCell(orderItem.getProduct().getProductName());
            orderTable.addCell(String.valueOf(orderItem.getProduct().getPrice()));
            orderTable.addCell(String.valueOf(orderItem.getQuantity()));
            double itemPayment = orderItem.getProduct().getPrice() * orderItem.getQuantity();
            orderTable.addCell(String.valueOf(itemPayment));
        }

        document.add(orderTable);
    }

    private void addFooter(Document document) throws DocumentException {
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBorder(Rectangle.NO_BORDER);
        // Add footer information such as total amount, payment details, etc.
        footerTable.addCell(footerCell);

        document.add(footerTable);
    }

    // Inner class for header and footer
    private class HeaderFooterPageEvent extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            float[] columnWidths = {2f, 1f, 1f}; // Example widths for 3 columns

            PdfPTable headerTable = new PdfPTable(columnWidths);
            headerTable.setWidthPercentage(100f);

            PdfPCell leftCell = new PdfPCell(new Phrase("Left Header - Seller Information"));
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell centerCell = new PdfPCell();
            ClassPathResource resource = new ClassPathResource("static/images/n.png");
            try {
                Image logo = Image.getInstance(resource.getURL());
                logo.scaleToFit(60f, 60f);
                centerCell.addElement(logo);
                centerCell.setBorder(Rectangle.NO_BORDER);
                centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            } catch (IOException | BadElementException e) {
                e.printStackTrace();
            }

            PdfPCell rightCell = new PdfPCell(new Phrase(getFormattedDate()));
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            headerTable.addCell(leftCell);
            headerTable.addCell(centerCell);
            headerTable.addCell(rightCell);

            float tableWidth = document.right() - document.leftMargin();
            headerTable.setTotalWidth(tableWidth); // Set the table width to fit within the document margins

            headerTable.writeSelectedRows(0, -1, document.left(), document.top() + 10, writer.getDirectContent());
        }
    }

    private String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
