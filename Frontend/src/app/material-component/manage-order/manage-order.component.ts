import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { error } from 'console';
import {saveAs} from 'file-saver';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { BillService } from 'src/app/services/bill.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstant } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-manage-order',
  templateUrl: './manage-order.component.html',
  styleUrls: ['./manage-order.component.scss']
})
export class ManageOrderComponent implements OnInit {

  displayedColumns: string[] = ['name', 'category', 'price', 'quantity', 'total', 'edit'];
  dataSource: any = [];
  categorys: any = [];
  products: any = [];
  price: any;
  totalAmount: number = 0;
  manageOrderForm: any = FormBuilder;
  responseMessage: any;


  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private snackBarService: SnackbarService,
    private formbuilder: FormBuilder,
    private ngxService: NgxUiLoaderService,
    private billService: BillService
  ) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.getCategroys();
    this.manageOrderForm = this.formbuilder.group({
      name: [null, [Validators.required, Validators.pattern(GlobalConstant.nameRegex)]],
      email: [null, [Validators.required, Validators.pattern(GlobalConstant.emailRegex)]],
      contactNumber: [null, [Validators.required, Validators.pattern(GlobalConstant.contactNumberRegex)]],
      paymentMethod: [null, [Validators.required]],
      product: [null, [Validators.required]],
      category: [null, [Validators.required]],
      quantity: [null, [Validators.required, Validators.pattern('^[1-9]\\d*$')]],  // Only positive integers
      price: [null, [Validators.required, Validators.pattern('^[0-9]*\\.?[0-9]+$')]], // Positive decimal number
      total: [0, [Validators.required, Validators.pattern('^[0-9]*\\.?[0-9]+$')]]  // Positive decimal number
    })

  }

  getCategroys() {
    this.categoryService.getFilteredCategorys().subscribe((response: any) => {
      this.ngxService.stop();
      this.categorys = response;
    }, (error: any) => {
      this.ngxService.stop();
      this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
    })
  }


  getProductsByCategory(value: any) {
    this.productService.getProductByCategory(value.id).subscribe((response: any) => {
      this.products = response;
      this.manageOrderForm.controls['price'].setValue('');
      this.manageOrderForm.controls['quantity'].setValue('');
      this.manageOrderForm.controls['total'].setValue(0);
    }, (error: any) => {
      this.ngxService.stop();
      this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
    })
  }


  getProductsDetails(value: any) {
    this.productService.getProductById(value.id).subscribe((response: any) => {
      // console.log('API Response:', response);
      
      // Check if response has products and the price field
      if (response && response.length > 0 && response[0].price) {
        this.price = response[0].price;  // Access price from the first element of the array
        this.manageOrderForm.controls['price'].setValue(this.price);
        this.manageOrderForm.controls['quantity'].setValue('1');
        this.manageOrderForm.controls['total'].setValue(this.price);  // Set total based on price
      }
      //  else {
        // this.snackBarService.openSnackBar("Price not available for the selected product", GlobalConstant.error);
      // }
    }, (error: any) => {
      this.ngxService.stop();
      this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
    });
  }
  
  
  


  setQuantity(value: any) {
    const quantity = this.manageOrderForm.controls['quantity'].value;
    if (quantity && !isNaN(quantity) && quantity > 0) {
      const total = quantity * this.manageOrderForm.controls['price'].value;
      this.manageOrderForm.controls['total'].setValue(total);
    }
    //  else {
    //   this.manageOrderForm.controls['quantity'].setValue('1');
    //   this.manageOrderForm.controls['total'].setValue(this.manageOrderForm.controls['price'].value);
    // }
  }
  
  



  validateProductAdd() {
    if (this.manageOrderForm.controls['total'].value === 0 || this.manageOrderForm.controls['total'].value === null
      || this.manageOrderForm.controls['quantity'].value <= 0
    ) {
      return true;
    }
    else {
      return false;
    }
  }



  validateSubmit() {
    if (this.totalAmount === 0 || this.manageOrderForm.controls['name'].value === null
      || this.manageOrderForm.controls['email'].value === null
      || this.manageOrderForm.controls['contactNumber'].value === null
      || this.manageOrderForm.controls['paymentMethod'].value === null
    ) {
      return true;
    }
    else {
      return false;
    }
  }

    // Add product to the order
    add() {
      const formData = this.manageOrderForm.value;
      const productExists = this.dataSource.find((e: { id: number }) => e.id === formData.product.id);
  
      if (!productExists) {
        this.totalAmount += formData.total;
        this.dataSource.push({
          id: formData.product.id,
          name: formData.product.name,
          category: formData.category.name,
          quantity: formData.quantity,
          price: formData.price,
          total: formData.total
        });
        this.dataSource = [...this.dataSource];
        this.snackBarService.openSnackBar(GlobalConstant.productAdded, 'success');
      } else {
        this.snackBarService.openSnackBar(GlobalConstant.productExistError, GlobalConstant.error);
      }
    }

     // Remove a product from the order
  handleDeleteAction(index: number, element: any) {
    this.totalAmount -= element.total;
    this.dataSource.splice(index, 1);
    this.dataSource = [...this.dataSource];
  }

  submitAction() {
    const formData = this.manageOrderForm.value;
    const data = {
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      paymentMethod: formData.paymentMethod,
      totalAmount: this.totalAmount.toString(),
      productDetails: JSON.stringify(this.dataSource)
    };

    this.ngxService.start();
    this.billService.generateReport(data).subscribe(
      (response: any) => {
        this.manageOrderForm.reset();
        this.dataSource = [];
        this.totalAmount = 0;
        this.snackBarService.openSnackBar('Bill submitted successfully', 'success');
        this.ngxService.stop(); 
      },
      (error: any) => {
        this.ngxService.stop();
        this.responseMessage = error.error?.message || GlobalConstant.genericMessage;
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    );
  }
  

  // // Download the generated bill PDF
  // downloadFile(fileName: string) {
  //   const data = { uuid: fileName };
  //   this.billService.getPdf(data).subscribe((response: any) => {
  //     saveAs(response, fileName + '.pdf');
  //     this.ngxService.stop();
  //   });
  // }

}







  
  



 









  

  



