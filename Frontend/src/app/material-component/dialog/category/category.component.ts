import { Component, Inject, OnInit, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';  // Added Validators for form validation
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstant } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {

  @Output() onAddCategory = new EventEmitter<any>();  // Emitting event when category is added
  @Output() onEditCategory = new EventEmitter<any>(); // Emitting event when category is edited
  categoryForm:any= FormGroup;  // Form group for category
  dialogAction: string = "Add";  // Action to determine whether it's add or edit
  action: string = "Add";  // Action string for category

  responseMessage: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    public dialogRef: MatDialogRef<CategoryComponent>,
    private snackBarServie: SnackbarService
  ) { }

  ngOnInit(): void {
    this.categoryForm = this.formBuilder.group({
      name: [null, Validators.required]
    });

    if (this.dialogData.action === 'Edit') {
      this.dialogAction = "Edit";  // Set to "Edit" if category data exists
      this.action = "Update";  // Action is now Edit
      this.categoryForm.patchValue({
        name: this.dialogData.data.name
      });  // Pre-fill form with category data
    }
  }

  // Submit the form data (Add or Edit)
  handleSubmit() {
    if (this.categoryForm.valid) {
      if (this.dialogAction === "Edit") {
        this.edit();
      } else {
        this.add();
      }
    }
  }

  // Add new category
  private add() {
    const formData = this.categoryForm.value;
    const data = {
      name: formData.name
    };
    this.categoryService.add(data).subscribe({
      next: (response: any) => {
        this.dialogRef.close();
        this.onAddCategory.emit(response);
        this.responseMessage = response.message;
        this.snackBarServie.openSnackBar(this.responseMessage, 'Category added successfully!');
      },
      error: (err) => {
        this.dialogRef.close();
        console.error(err);
        this.responseMessage = err.error?.message || GlobalConstant.genericMessage;
        this.snackBarServie.openSnackBar(this.responseMessage, 'Failed to add category!');
      }
    });
  }

  // Edit existing category
  private edit() {
    const formData = this.categoryForm.value;
    const data = {
      id: this.dialogData.data.id,
      name: formData.name
    };
    this.categoryService.update(data).subscribe({
      next: (response: any) => {
        this.dialogRef.close();
        this.onEditCategory.emit(response);
        this.responseMessage = response.message;
        this.snackBarServie.openSnackBar(this.responseMessage, 'Category updated successfully!');
      },
      error: (err) => {
        this.dialogRef.close();
        console.error(err);
        this.responseMessage = err.error?.message || GlobalConstant.genericMessage;
        this.snackBarServie.openSnackBar(this.responseMessage, 'Failed to update category!');
      }
    });
  }
}
