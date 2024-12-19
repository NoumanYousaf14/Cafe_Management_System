import { Component, AfterViewInit } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from '../services/snackbar.service';
import { GlobalConstant } from '../shared/global-constants';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements AfterViewInit {
  responseMessage: any;
  data: { category?: number; product?: number; bill?: number } = {};

  constructor(
    private dashboardService: DashboardService,
    private ngxService: NgxUiLoaderService,
    private snackBarService: SnackbarService
  ) {
    this.ngxService.start();
    this.dashboardData();
  }

  ngAfterViewInit() {}

  dashboardData() {
	this.dashboardService.getDetails().subscribe({
	  next: (response: any) => {
		this.ngxService.stop();
		// console.log('API Response:', response);  // Log the raw API response
  
		// Initialize data as an empty object
		this.data = {} as { [key: string]: number };
  
		// Remove trailing spaces from keys and set data
		Object.keys(response).forEach(key => {
		  const cleanedKey = key.trim(); // Remove spaces from keys
		  (this.data as { [key: string]: number })[cleanedKey.toLowerCase()] = response[key];
		});
  
		// Log the processed data
		// console.log('Processed Data:', this.data);
	  },
	  error: (error: any) => {
		this.ngxService.stop();
		this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
		this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
	  }
	});
  }
  
  
}
