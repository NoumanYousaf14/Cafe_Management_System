import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstant } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {

  displayedColumns: string[] = ['name', 'email', 'contactNumber', 'status'];
  dataSource: any;
  responseMessage: any;

  constructor(
    private userService: UserService,
    private snackBarService: SnackbarService,
    private ngxService: NgxUiLoaderService
  ) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData();
  }

  // Fetch user data for the table
  tableData() {
    this.userService.getUsers().subscribe(
      (response: any) => {
        this.ngxService.stop();
        // Ensure the status is interpreted as a boolean for the toggle
        response.forEach((user: any) => {
          user.status = user.status === 'true';
        });
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

  // Handle status change for a user (Active/Inactive toggle)
  onChange(status: boolean, id: any) {
    this.ngxService.start();
    const data = {
      id: id,
      status: status ? 'true' : 'false', // Convert boolean to string for backend
    };
  
    this.userService.update(data).subscribe(
      (response: any) => {
        this.ngxService.stop();
        this.snackBarService.openSnackBar('User Status updated successfully', 'Success');
      },
      (error: any) => {
        this.ngxService.stop();
        this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    );
  }
}
