import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { CategoryComponent } from '../category/category.component';
import { ProductService } from 'src/app/services/product.service';
import { GlobalConstant } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss']
})
export class ProductComponent implements OnInit {

    @Output() onAddProduct = new EventEmitter<any>();  // Emitting event when product is added
    @Output() onEditProduct = new EventEmitter<any>(); // Emitting event when product is edited
    productForm:any= FormGroup;  // Form group for product
    dialogAction: string = "Add";  // Action to determine whether it's add or edit
    action: string = "Add";  // Action string for product
    categorys:any=[];
    responseMessage: any;
  

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
        private formBuilder: FormBuilder,
        private productService:ProductService,
        private categoryService: CategoryService,
        public dialogRef: MatDialogRef<ProductComponent>,
        private snackBarServie: SnackbarService
  ) { }

  ngOnInit(): void {

    this.productForm = this.formBuilder.group({
          name: [null, [Validators.required,Validators.pattern(GlobalConstant.nameRegex)]],
          categoryId: [null, [Validators.required]],
          price: [null, [Validators.required]],
          description: [null, [Validators.required]]
        });
    
        if (this.dialogData.action === 'Edit') {
          this.dialogAction = "Edit";  // Set to "Edit" if category data exists
          this.action = "Update";  // Action is now Edit
          this.productForm.patchValue(this.dialogData.data);  // Pre-fill form with category data
        }
        this.getCategorys();
  }

  // fucnt to get cotegorys
  getCategorys(){
    this.categoryService.getCategorys().subscribe({
      next: (response: any) => {
        this.categorys=response;
      },
      error: (err) => {
        this.dialogRef.close();
        console.error(err);
        this.responseMessage = err.error?.message || GlobalConstant.genericMessage;
        this.snackBarServie.openSnackBar(this.responseMessage,GlobalConstant.error);
      }
    });
  }

    // Submit the form data (Add or Edit)
    handleSubmit() {
      if (this.productForm.valid) {
        if (this.dialogAction === "Edit") {
          this.edit();
        } else {
          this.add();
        }
      }
    }

    // Add new product
  private add() {
    const formData = this.productForm.value;
    const data = {
      name: formData.name,
      categoryId:formData.categoryId,
      price:formData.price,
      description:formData.description
    };
    this.productService.add(data).subscribe({
      next: (response: any) => {
        this.dialogRef.close();
        this.onAddProduct.emit(response);
        this.responseMessage = response.message;
        this.snackBarServie.openSnackBar(this.responseMessage, 'Product added successfully!');
      },
      error: (err) => {
        this.dialogRef.close();
        console.error(err);
        this.responseMessage = err.error?.message || GlobalConstant.genericMessage;
        this.snackBarServie.openSnackBar(this.responseMessage, 'Failed to add product!');
      }
    });
  }




    // Edit existing product
    private edit() {
      const formData = this.productForm.value;
      const data = {
        id: this.dialogData.data.id,
        name: formData.name,
        categoryId:formData.categoryId,
        price:formData.price,
        description:formData.description
      };
      this.productService.update(data).subscribe({
        next: (response: any) => {
          this.dialogRef.close();
          this.onEditProduct.emit(response);
          this.responseMessage = response.message;
          this.snackBarServie.openSnackBar(this.responseMessage, 'product updated successfully!');
        },
        error: (err) => {
          this.dialogRef.close();
          console.error(err);
          this.responseMessage = err.error?.message || GlobalConstant.genericMessage;
          this.snackBarServie.openSnackBar(this.responseMessage, 'Failed to update product!');
        }
      });
    }
}
