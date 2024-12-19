import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstant } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  // change password form 
  oldPassword = true;
  newPassword = true;
  confirmPassword = true;
  changePasswordForm: any = FormGroup;
  responseMessage: any;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private snackBarServie: SnackbarService,
    public dialogRef: MatDialogRef<ChangePasswordComponent>,
    private ngxService: NgxUiLoaderService,

  ) { }

  ngOnInit(): void {
    this.changePasswordForm = this.formBuilder.group({
      oldPassword: [null, [Validators.required]],
      newPassword: [null, [Validators.required]],
      confirmPassword: [null, [Validators.required]],
    });
  }

    // function to validate the submitted passwords
    validateSubmit(){
      if (this.changePasswordForm.controls['newPassword'].value !=this.changePasswordForm.controls['confirmPassword'].value) {
        return true;
      }
      else{
        return false;
      }
    }

     // functiont to handel the submit to call api
      handleChangePasswordSubmit() {
        this.ngxService.start();
        const formData = this.changePasswordForm.value;
      
        const data = {
          oldPassword: formData.oldPassword,
          newPassword: formData.newPassword,
          confirmPassword: formData.confirmPassword,
          
        };
      
        this.userService.changePassword(data).subscribe({
          next: (response: any) => {
            this.ngxService.stop();
            console.log('change password success response', response); // Log the response
            this.responseMessage = response?.message || 'Password changed successfully!';
            this.snackBarServie.openSnackBar(this.responseMessage, 'success');
            this.dialogRef.close(); // Close form on success
        
          },
          error: (error) => {
            this.ngxService.stop();
            console.error('change password Error:', error); // Log the error
            if (error.error?.message) {
              this.responseMessage = error.error?.message ;
             }
              else{
                this.responseMessage = GlobalConstant.genericMessage ;
              }
              this.snackBarServie.openSnackBar(this.responseMessage, GlobalConstant.error);
          }
        });
      }

}
