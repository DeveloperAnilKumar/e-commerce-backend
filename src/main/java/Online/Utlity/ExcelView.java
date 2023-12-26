package Online.Utlity;

import Online.Entity.Order;
import Online.Entity.OrderItems;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.List;
import java.util.Map;

public class ExcelView extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<Order> orders = (List<Order>) model.get("orders");

        Sheet sheet = workbook.createSheet("Orders");
        sheet.setDefaultColumnWidth(15);

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font totalFont = workbook.createFont();
        totalFont.setBold(true);

        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setFont(totalFont);

        String[] headerTitles = {"Order ID", "Order Date", "User Name", "User Email",
                "Product Name", "Product Price", "Quantity", "Payment Method", "Total Payment"};

        Row header = sheet.createRow(0);
        for (int i = 0; i < headerTitles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headerTitles[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        double grandTotalPayment = 0.0;

        for (Order order : orders) {
            double orderTotalPayment = 0.0; // Initialize total payment for each order

            for (OrderItems orderItem : order.getOrderItems()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getId().toString());
                row.createCell(1).setCellValue(order.getOrderDate().toString());
                row.createCell(2).setCellValue(order.getUser().getFullName());
                row.createCell(3).setCellValue(order.getUser().getEmail());
                row.createCell(4).setCellValue(orderItem.getProduct().getProductName());
                row.createCell(5).setCellValue(orderItem.getProduct().getPrice());
                row.createCell(6).setCellValue(orderItem.getQuantity());
                row.createCell(7).setCellValue(order.getPaymentMethod()); // Adding Payment Method column

                double itemPayment = orderItem.getProduct().getPrice() * orderItem.getQuantity();
                row.createCell(8).setCellValue(itemPayment); // Set the Total Payment value in the cell
                orderTotalPayment += itemPayment;
            }

            Row orderTotalRow = sheet.createRow(rowNum++);
            Cell orderTotalLabelCell = orderTotalRow.createCell(7);
            orderTotalLabelCell.setCellValue("Order Total:");
            orderTotalLabelCell.setCellStyle(totalStyle);

            Cell orderTotalValueCell = orderTotalRow.createCell(8);
            orderTotalValueCell.setCellValue(orderTotalPayment);
            orderTotalValueCell.setCellStyle(currencyStyle);

            grandTotalPayment += orderTotalPayment;
        }

        Row grandTotalRow = sheet.createRow(rowNum);
        Cell grandTotalLabelCell = grandTotalRow.createCell(7);
        grandTotalLabelCell.setCellValue("Grand Total:");
        grandTotalLabelCell.setCellStyle(totalStyle);

        Cell grandTotalValueCell = grandTotalRow.createCell(8);
        grandTotalValueCell.setCellValue(grandTotalPayment);
        grandTotalValueCell.setCellStyle(currencyStyle);

        response.setHeader("Content-Disposition", "attachment; filename=\"orders.xlsx\"");
    }
}

