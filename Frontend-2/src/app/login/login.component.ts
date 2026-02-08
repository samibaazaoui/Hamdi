import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ForgotPasswordComponent } from '../forgot-password/forgot-password.component';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { UserService } from '../services/user.service';
import { SnackbarService } from '../services/snackbar.service';
import { GlobalConstants } from '../shared/global-constants';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    loginForm:any=FormGroup;
    responseMessage:any;
    password =true;
    confirmPassword=true;
    constructor(private formBuilder:FormBuilder,
      private dialogRef:MatDialogRef<ForgotPasswordComponent>,
      private ngxService:NgxUiLoaderService,
      private userService:UserService,
      private snackbarService:SnackbarService,
      private router:Router
  
    ) { }
  
    ngOnInit(): void {
      this.loginForm=this.formBuilder.group({
        email:[null,[Validators.required,Validators.pattern(GlobalConstants.emailRegex)]],
        password:[null,[Validators.required]]
      });
    }
   handleSubmit(){
      this.ngxService.start();
      var formData=this.loginForm.value;
      var data= {
        email:formData.email,
        password:formData.password
      };
  
      this.userService.login(data).subscribe((response:any)=>{
        this.ngxService.stop();
        this.dialogRef.close();
        localStorage.setItem('token',response.token);
        this.router.navigate(['/cafe/dashboard']);
        this.responseMessage= response?.message;
      },(error)=>{
        this.ngxService.stop();
        if(error.error?.message){
          this.responseMessage=error.error?.message;
        }
        else{
          this.responseMessage=GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage,GlobalConstants.error);
      }
    )
    }

}
