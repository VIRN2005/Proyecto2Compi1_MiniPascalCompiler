; ModuleID = 'MiniPascal'
source_filename = "C:\Users\wilme\Downloads\PascalCompiler_Compi2\PascalCompiler\Programs\IntOp.txt"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._tc8ea1d48_dc80_4f36_a986_18071c86cef8 = private unnamed_addr constant [4 x i8] c"X: \00"

@.str._t441d3c8f_ba5c_4b96_872a_bb0835cb90fd = private unnamed_addr constant [4 x i8] c"Y: \00"

@.str._td67e846c_f677_401e_bc6e_b0d0c374c533 = private unnamed_addr constant [4 x i8] c"Z: \00"

@.str._t818074bd_29d3_43b5_b135_c2da8768dec7 = private unnamed_addr constant [4 x i8] c"A: \00"

@.str._t49fe7acb_7670_4969_be31_89edf4a6e7e8 = private unnamed_addr constant [7 x i8] c"Mult: \00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %IntOp
IntOp:
%a = alloca i32
store i32 0, i32* %a
%x = alloca i32
store i32 0, i32* %x
%y = alloca i32
store i32 0, i32* %y
%z = alloca i32
store i32 0, i32* %z
%multiply = alloca i32
store i32 0, i32* %multiply
%_t0 = alloca i32
store i32 100, i32* %_t0
store i32 100, i32* %x
%_t1 = alloca i32
store i32 50, i32* %_t1
store i32 50, i32* %y
%_t2 = alloca i32
store i32 10, i32* %_t2
store i32 10, i32* %z
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._tc8ea1d48_dc80_4f36_a986_18071c86cef8, i32 0, i32 0))

%_t4 = alloca i32
store i32 100, i32* %_t4
%_t4e6b9636_1998_4a1c_9788_b940ec37d041 = load i32, i32* %x
call void @write_int(i32 %_t4e6b9636_1998_4a1c_9788_b940ec37d041)

call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t441d3c8f_ba5c_4b96_872a_bb0835cb90fd, i32 0, i32 0))

%_t6 = alloca i32
store i32 50, i32* %_t6
%_t91a88f8d_6076_4ba6_99bb_5ac5795020e2 = load i32, i32* %y
call void @write_int(i32 %_t91a88f8d_6076_4ba6_99bb_5ac5795020e2)

call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._td67e846c_f677_401e_bc6e_b0d0c374c533, i32 0, i32 0))

%_t8 = alloca i32
store i32 10, i32* %_t8
%_t7cb289ae_cac1_44a1_bad2_461e618cdbb9 = load i32, i32* %z
call void @write_int(i32 %_t7cb289ae_cac1_44a1_bad2_461e618cdbb9)

%_t9 = alloca i32
store i32 5, i32* %_t9
store i32 5, i32* %a
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t818074bd_29d3_43b5_b135_c2da8768dec7, i32 0, i32 0))

%_t11 = alloca i32
store i32 5, i32* %_t11
%_te2ba61ec_6384_4463_a7e3_dcef05c80fa0 = load i32, i32* %a
call void @write_int(i32 %_te2ba61ec_6384_4463_a7e3_dcef05c80fa0)

%_t12 = alloca i32
store i32 5, i32* %_t12
%_t13 = alloca i32
store i32 10, i32* %_t13
%a_val = load i32, i32* %a
%z_val = load i32, i32* %z
%_t14_val = mul i32 %a_val, %z_val
store i32 %_t14_val, i32* %multiply
call void @write_string(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str._t49fe7acb_7670_4969_be31_89edf4a6e7e8, i32 0, i32 0))

%_t16 = alloca i32
store i32 %_t14_val, i32* %_t16
call void @write_int(i32 %_t14_val)

ret i32 0
}
define void @write_double(double %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @double_fmt, i32 0, i32 0), double %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_int(i32 %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @str_fmt, i32 0, i32 0), i32 %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_string(i8* %str) {
  %str_ptr = alloca i8*
  store i8* %str, i8** %str_ptr
  %str_val = load i8*, i8** %str_ptr
  call i32 @puts(i8* %str_val)
  ret void
}
define i32 @read() {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  %stdin_val = load %struct._IO_FILE*, %struct._IO_FILE** @stdin
  %result = call i8* @fgets(i8* %buf_ptr, i32 32, %struct._IO_FILE* %stdin_val)
  %num = call i32 @atoi(i8* %buf_ptr)
  ret i32 %num
}
declare i32 @atoi(i8*)
declare i32 @sprintf(i8*, i8*, ...)
declare i32 @puts(i8*)
declare i8* @fgets(i8*, i32, %struct._IO_FILE*)
declare void @exit(i32)

; Function Attrs: noinline nounwind optnone uwtable
declare i32 @printf(i8*, ...) #0

attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 10.0.0-4ubuntu1 "}
