package com.example.utils

import com.example.data.OrderEntity

object HtmlGenerator {
    fun generateInvoiceHtml(order: OrderEntity, shopName: String, shopAddress: String, shopPhone: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Invoice - ${order.orderId}</title>
                <style>
                    @media print {
                        @page { size: A4; margin: 10mm; }
                        body { width: 190mm; height: 277mm; margin: 0; padding: 0; box-sizing: border-box; overflow: hidden; }
                    }
                    body { font-family: Arial, sans-serif; padding: 20px; font-size: 14px; }
                    .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #000; padding-bottom: 10px; }
                    .header h1 { font-size: 24px; margin: 5px 0; }
                    .header p { margin: 5px 0; }
                    .header h2 { font-size: 18px; margin: 10px 0; }
                    .details { margin-bottom: 15px; }
                    .details p { margin: 5px 0; }
                    .customer { background: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 15px; }
                    .customer h3 { font-size: 16px; margin-top: 0; margin-bottom: 10px; }
                    .customer p { margin: 5px 0; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                    th, td { border: 1px solid #000; padding: 8px; text-align: left; }
                    th { background: #2c3e50; color: white; }
                    .summary { background: #f8f9fa; padding: 15px; border-radius: 5px; }
                    .summary h3 { font-size: 16px; margin-top: 0; margin-bottom: 10px; }
                    .summary p { margin: 5px 0; }
                    .footer { text-align: center; margin-top: 20px; padding-top: 15px; border-top: 1px dashed #000; }
                    .footer p { margin: 5px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>$shopName</h1>
                    <p>$shopAddress</p>
                    <p>ফোন (Phone): $shopPhone</p>
                    <h2>অর্ডার ইনভয়েস (Order Invoice)</h2>
                </div>
                
                <div class="details">
                    <p><strong>অর্ডার আইডি (Order ID):</strong> ${order.orderId}</p>
                    <p><strong>অর্ডার তারিখ (Date):</strong> ${order.orderDate}</p>
                    <p><strong>ডেলিভারি (Delivery):</strong> ${order.deliveryDate}</p>
                    <p><strong>স্ট্যাটাস (Status):</strong> ${order.status}</p>
                </div>
                
                <div class="customer">
                    <h3>গ্রাহক তথ্য (Customer)</h3>
                    <p><strong>নাম (Name):</strong> ${order.customerName}</p>
                    <p><strong>ফোন (Phone):</strong> ${order.phone}</p>
                    <p><strong>ঠিকানা (Address):</strong> ${order.address}</p>
                </div>
                
                <table>
                    <tr>
                        <th>আইটেম (Item)</th>
                        <th>পরিমাণ (Quantity)</th>
                        <th>মাপ (Measurements)</th>
                        <th>মূল্য (Price)</th>
                    </tr>
                    <tr>
                        <td>${order.itemName}</td>
                        <td>${order.quantity}</td>
                        <td>${order.measurements}</td>
                        <td>৳ ${order.totalAmount}</td>
                    </tr>
                </table>
                
                <div class="summary">
                    <h3>পেমেন্ট সারাংশ (Payment)</h3>
                    <p><strong>মোট টাকা (Total):</strong> ৳ ${order.totalAmount}</p>
                    <p><strong>অগ্রিম (Advance):</strong> ৳ ${order.advancePaid}</p>
                    <p><strong>বাকি টাকা (Due):</strong> ৳ ${order.dueAmount}</p>
                </div>
                
                <div class="footer">
                    <p>ধন্যবাদান্তে (Thank You)</p>
                    <p><strong>$shopName</strong></p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateSalesReportHtml(
        shopName: String, shopAddress: String, shopPhone: String,
        fromDate: String, toDate: String,
        totalSale: Double, totalAdvance: Double, totalDue: Double
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Sales Report</title>
                <style>
                    @media print {
                        @page { size: A4; margin: 10mm; }
                        body { width: 190mm; height: 277mm; margin: 0; padding: 0; box-sizing: border-box; overflow: hidden; page-break-inside: avoid; }
                    }
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #000; padding-bottom: 10px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #000; padding: 10px; text-align: left; }
                    th { background: #f0f0f0; }
                    .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #000; display: flex; justify-content: space-between; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>$shopName</h1>
                    <p>$shopAddress</p>
                    <p>ফোন: $shopPhone</p>
                    <h2>বিক্রয় রিপোর্ট (Sales Report)</h2>
                </div>
                
                <p><strong>রিপোর্ট সময়কাল (Period):</strong> ${fromDate.ifEmpty { "সব তারিখ" }} থেকে ${toDate.ifEmpty { "সব তারিখ" }}</p>
                
                <table>
                    <tr>
                        <th>বিবরণ (Description)</th>
                        <th>পরিমাণ (Amount)</th>
                    </tr>
                    <tr>
                        <td>মোট বিক্রয় (Total Sales)</td>
                        <td style="text-align: right;">৳ $totalSale</td>
                    </tr>
                    <tr>
                        <td>মোট অগ্রিম (Total Advance)</td>
                        <td style="text-align: right;">৳ $totalAdvance</td>
                    </tr>
                    <tr>
                        <td>বাকি টাকা (Due Amount)</td>
                        <td style="text-align: right;">৳ $totalDue</td>
                    </tr>
                </table>
                
                <div class="footer">
                    <span>প্রস্তুতকারকের স্বাক্ষর</span>
                    <span>তারিখ: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())}</span>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateAccountingReportHtml(
        shopName: String, shopAddress: String, shopPhone: String,
        fromDate: String, toDate: String,
        totalSale: Double, totalWorkerPayment: Double, netProfit: Double
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Accounting Report</title>
                <style>
                    @media print {
                        @page { size: A4; margin: 10mm; }
                        body { width: 190mm; height: 277mm; margin: 0; padding: 0; box-sizing: border-box; overflow: hidden; page-break-inside: avoid; }
                    }
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #000; padding-bottom: 10px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #000; padding: 10px; text-align: left; }
                    th { background: #f0f0f0; }
                    .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #000; display: flex; justify-content: space-between; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>$shopName</h1>
                    <p>$shopAddress</p>
                    <p>ফোন: $shopPhone</p>
                    <h2>অ্যাকাউন্টিং রিপোর্ট (Accounting Report)</h2>
                </div>
                
                <p><strong>রিপোর্ট সময়কাল (Period):</strong> ${fromDate.ifEmpty { "সব তারিখ" }} থেকে ${toDate.ifEmpty { "সব তারিখ" }}</p>
                
                <table>
                    <tr>
                        <th>বিবরণ (Description)</th>
                        <th>পরিমাণ (Amount)</th>
                    </tr>
                    <tr>
                        <td>মোট বিক্রয় (Total Sales)</td>
                        <td style="text-align: right;">৳ $totalSale</td>
                    </tr>
                    <tr>
                        <td>কর্মী বেতন/অগ্রিম (Worker Payments)</td>
                        <td style="text-align: right;">৳ $totalWorkerPayment</td>
                    </tr>
                    <tr>
                        <td><strong>নেট লাভ (Net Profit)</strong></td>
                        <td style="text-align: right;"><strong>৳ $netProfit</strong></td>
                    </tr>
                </table>
                
                <div class="footer">
                    <span>প্রস্তুতকারকের স্বাক্ষর</span>
                    <span>তারিখ: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())}</span>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateWorkerReportHtml(
        shopName: String, shopAddress: String, shopPhone: String,
        workerName: String,
        fromDate: String, toDate: String,
        totalWork: Int, totalSalary: Double, advancePaid: Double, dueAmount: Double
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Worker Report</title>
                <style>
                    @media print {
                        @page { size: A4; margin: 10mm; }
                        body { width: 190mm; height: 277mm; margin: 0; padding: 0; box-sizing: border-box; overflow: hidden; page-break-inside: avoid; }
                    }
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #000; padding-bottom: 10px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #000; padding: 10px; text-align: left; }
                    th { background: #f0f0f0; }
                    .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #000; display: flex; justify-content: space-between; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>$shopName</h1>
                    <p>$shopAddress</p>
                    <p>ফোন: $shopPhone</p>
                    <h2>কর্মী রিপোর্ট (Worker Report)</h2>
                </div>
                
                <p><strong>কর্মী (Worker):</strong> $workerName</p>
                <p><strong>রিপোর্ট সময়কাল (Period):</strong> ${fromDate.ifEmpty { "সব তারিখ" }} থেকে ${toDate.ifEmpty { "সব তারিখ" }}</p>
                
                <table>
                    <tr>
                        <th>বিবরণ (Description)</th>
                        <th>পরিমাণ (Amount)</th>
                    </tr>
                    <tr>
                        <td>মোট কাজ (Total Work)</td>
                        <td style="text-align: right;">$totalWork</td>
                    </tr>
                    <tr>
                        <td>মোট বেতন (Total Salary)</td>
                        <td style="text-align: right;">৳ $totalSalary</td>
                    </tr>
                    <tr>
                        <td>অগ্রিম (Advance)</td>
                        <td style="text-align: right;">৳ $advancePaid</td>
                    </tr>
                    <tr>
                        <td>বাকি (Due Amount)</td>
                        <td style="text-align: right;">৳ $dueAmount</td>
                    </tr>
                </table>
                
                <div class="footer">
                    <span>প্রস্তুতকারকের স্বাক্ষর</span>
                    <span>তারিখ: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())}</span>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
