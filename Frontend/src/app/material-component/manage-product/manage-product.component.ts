import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstant } from 'src/app/shared/global-constants';
import { ProductComponent } from '../dialog/product/product.component';
import { ConfirmationComponent } from '../dialog/confirmation/confirmation.component';

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss'],
})
export class ManageProductComponent implements OnInit {
  displayedColumns: string[] = ['name', 'categoryName', 'description', 'price', 'edit'];
  dataSource: any;
  responseMessage:any;

  constructor(
    private productService: ProductService,
    private snackBarService: SnackbarService,
    private dialog: MatDialog,
    private ngxService: NgxUiLoaderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData(); // Load table data on initialization
  }

  // Fetch product data for the table
  tableData() {
    this.productService.getProducts().subscribe(
      (response: any) => {
        console.log(response)
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
  handleAddAction() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { action: 'Add' };
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(ProductComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    dialogRef.componentInstance.onAddProduct.subscribe(() => {
      this.tableData(); // Refresh table after adding a product
    });
  }

  // Handle editing an existing product
  handleEditAction(values: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { action: 'Edit', data: values };
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(ProductComponent, dialogConfig);

    dialogRef.componentInstance.onEditProduct.subscribe(() => {
      this.tableData(); // Refresh table after editing a product
    });
  }

  // Handle deleting a product
  handleDeleteAction(values: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { message: `delete ${values.name} product`, confirmation: true };

    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);

    dialogRef.componentInstance.onEmitStatusChange.subscribe(() => {
      this.ngxService.start();
      this.deleteProduct(values.id);
      dialogRef.close();
    });
  }

  // Delete a product by ID
  deleteProduct(id: any) {
    this.productService.delete(id).subscribe(
      (response: any) => {
        this.ngxService.stop();
        this.tableData();
        this.responseMessage = response?.message || 'Product deleted successfully';
        this.snackBarService.openSnackBar(this.responseMessage, 'Success');
      },
      (error: any) => {
        this.ngxService.stop();
        this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    );
  }

  // Handle status change for a product (Active/Inactive toggle)
  onChange(status: any, id: any) {
    this.ngxService.start();

    const data = {
      id: id,
      status: status, // Map the toggle state to Active/Inactive
    };

    this.productService.updateStatus(data).subscribe(
      () => {
        this.ngxService.stop();
        this.snackBarService.openSnackBar('Status updated successfully', 'Success');
         
      },
      (error: any) => {
        this.ngxService.stop();
        this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    );
  }
}
