import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstant } from 'src/app/shared/global-constants';
import { CategoryComponent } from '../dialog/category/category.component';

@Component({
  selector: 'app-manage-category',
  templateUrl: './manage-category.component.html',
  styleUrls: ['./manage-category.component.scss']
})
export class ManageCategoryComponent implements OnInit {

  displayedColumns: string[] = ['name', 'edit'];
  dataSource: any;
  responseMessage: any;

  constructor(
    private categoryService: CategoryService,
    private snackBarServie: SnackbarService,
    private dialog: MatDialog,
    private ngxService: NgxUiLoaderService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData();
  }

  // Fetch data for table
  tableData() {
    this.categoryService.getCategorys().subscribe((response: any) => {
      this.ngxService.stop();
      this.dataSource = new MatTableDataSource(response);
    }, (error: any) => {
      this.ngxService.stop();
      console.log(error.error?.message);
      this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
      this.snackBarServie.openSnackBar(this.responseMessage, GlobalConstant.error);
    });
  }

  // Apply filter to the category
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  // Handle Add category action
  handleAddAction() {
    const diaglogConfig = new MatDialogConfig();
    diaglogConfig.data = {
      action: "Add"
    };
    diaglogConfig.width = "850px";

    const dialogRef = this.dialog.open(CategoryComponent, diaglogConfig);

    dialogRef.componentInstance.onAddCategory.subscribe((response: any) => {
      this.tableData();  // Refresh data after adding
    });
  }

  // Handle Edit category action
  handleEditAction(values: any) {
    const diaglogConfig = new MatDialogConfig();
    diaglogConfig.data = {
      action: "Edit",
      data: values
    };
    diaglogConfig.width = "850px";

    const dialogRef = this.dialog.open(CategoryComponent, diaglogConfig);

    dialogRef.componentInstance.onEditCategory.subscribe((response: any) => {
      this.tableData();  // Refresh data after editing
    });
  }
}
