; ModuleID = 'MiniPascal'
source_filename = "C:\Users\Victo\OneDrive\Documents\- UNITEC\Ing. Sistemas\2025 - Periodo 1\Compiladores 1\Proyect 2\Proyecto2Compi1_MiniPascalCompiler\PascalCompiler\Programs\Salario.txt"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._t4ae8dc3a_07ee_407f_9dc3_f65ec6a90ce8 = private unnamed_addr constant [9 x i8] c"Salario:\00"

@.str._tb138303c_ef39_4034_a404_9e8fdb356a53 = private unnamed_addr constant [17 x i8] c"Estas pero pobre\00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %Salario
Salario:
%salDiario = alloca i32
store i32 0, i32* %salDiario
%dias = alloca i32
store i32 0, i32* %dias
%salarioTot = alloca i32
store i32 0, i32* %salarioTot
%_t0 = alloca i32
store i32 500, i32* %_t0
store i32 500, i32* %salDiario
%_t1 = alloca i32
store i32 30, i32* %_t1
store i32 30, i32* %dias
%_t2 = alloca i32
store i32 30, i32* %_t2
%_t3 = alloca i32
store i32 500, i32* %_t3
%dias_val = load i32, i32* %dias
%salDiario_val = load i32, i32* %salDiario
%_t4_val = mul i32 %dias_val, %salDiario_val
store i32 %_t4_val, i32* %salarioTot
call void @write_string(i8* getelementptr inbounds ([9 x i8], [9 x i8]* @.str._t4ae8dc3a_07ee_407f_9dc3_f65ec6a90ce8, i32 0, i32 0))

%_t6 = alloca i32
store i32 %_t4_val, i32* %_t6
call void @write_int(i32 %_t4_val)

%_t7 = alloca i32
store i32 %_t4_val, i32* %_t7
%_t8 = alloca i32
store i32 11500, i32* %_t8
%_t9f4316be_b33f_4d2e_86e6_81cca44263b4 = add i32 11500, 0
%_t9 = icmp slt i32 %_t4_val, %_t9f4316be_b33f_4d2e_86e6_81cca44263b4
br i1 %_t9, label %L_t9, label %Label0
L_t9:
call void @write_string(i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str._tb138303c_ef39_4034_a404_9e8fdb356a53, i32 0, i32 0))

br label %Label1
Label0:
br label %Label1
Label1:
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
