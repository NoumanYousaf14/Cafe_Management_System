import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { BillService } from 'src/app/services/bill.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstant } from 'src/app/shared/global-constants';
import { ViewBillProductsComponent } from '../dialog/view-bill-products/view-bill-products.component';
import { ConfirmationComponent } from '../dialog/confirmation/confirmation.component';
import * as saveAs from 'file-saver';

@Component({
  selector: 'app-view-bill',
  templateUrl: './view-bill.component.html',
  styleUrls: ['./view-bill.component.scss']
})
export class ViewBillComponent implements OnInit {

  displayedColumns: string[] = ['name', 'email', 'contactNumber', 'paymentMethod', 'total', 'view'];
  dataSource: any = [];
  responseMessage: any;

  constructor(
    private dialog: MatDialog,
    private snackBarService: SnackbarService,
    private router: Router,
    private ngxService: NgxUiLoaderService,
    private billService: BillService
  ) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData();
  }

  // Fetch product data for the table
  tableData() {
    this.billService.getBills().subscribe(
      (response: any) => {
        this.ngxService.stop();
        this.dataSource = new MatTableDataSource(response);
      },
      (error: any) => {
        this.ngxService.stop();
        this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    );
  }


  // Apply a filter to the table based on user input
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  // Handle adding a new product
  handleViewAction(values: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      data: values
    }
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(ViewBillProductsComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });
  }


  handleDeleteAction(value: any) {



    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      message: `delete ${value.name} bill`,
      confirmation: true
    };

    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);

    dialogRef.componentInstance.onEmitStatusChange.subscribe((response) => {
      this.ngxService.start();
      this.deleteBill(value.id);
      dialogRef.close();
    });

  }

  deleteBill(id: any) {
    this.billService.delete(id).subscribe(
      (response: any) => {
        this.ngxService.stop();
        this.tableData();
        this.responseMessage = response?.message || 'Bill deleted successfully';
        this.snackBarService.openSnackBar(this.responseMessage, 'Success');
      },
      (error: any) => {
        this.ngxService.stop();
        this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    );
  }

  downloadReportAction(value: any) {
    this.ngxService.start();
    var data = {
      name: value.name,
      email: value.email,
      uuid: value.uuid,
      contactNumber: value.contactNumber,
      paymentMethod: value.paymentMethod,
      totalAmount: value.totalAmount,
      productDetails: value.productDetails
    }
    this.downloadFile(value.uuid, data);
  }


  // Download the generated bill PDF
  downloadFile(fileName: string, data: any) {
    this.billService.getPdf(data).subscribe((response: any) => {
      saveAs(response, fileName + '.pdf');
      this.ngxService.stop();
    });
  }
}
